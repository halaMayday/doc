package com.mlcloud.fusioncloud.service;


import com.google.gson.Gson;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.exception.LoadConfigurationException;
import com.mlcloud.defination.exception.NoAvailableAgentException;
import com.mlcloud.defination.service.RestoreService;
import com.mlcloud.defination.service.Rollbackable;
import com.mlcloud.fusioncloud.bean.DetailVolumeResponse;
import com.mlcloud.fusioncloud.bean.InstanceModel;
import com.mlcloud.fusioncloud.bean.service.accept.RestoreParam;
import com.mlcloud.fusioncloud.bean.service.ret.RestoreReturn;
import com.mlcloud.fusioncloud.exception.*;
import com.mlcloud.local.LocalAccess;
import com.mlcloud.local.exception.mdfs.LocalSnapshotException;
import com.mlcloud.local.exception.os.LocalMakeDirectoryException;
import com.mlcloud.local.exception.os.LocalReadException;
import com.mlcloud.rpc.client.RemoteAccess;
import com.mlcloud.rpc.client.exception.AgentStatusErrorException;
import com.mlcloud.rpc.client.exception.AsyncTaskTimeoutException;
import com.mlcloud.rpc.client.exception.rados.RpcRbdImportException;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.openstack.compute.domain.NovaServer;
import org.openstack4j.openstack.storage.block.domain.CinderVolume;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：hf
 * @date ：Created in 2020/11/25 5:54 下午
 * @description：
 * @modified By：
 * @version: $
 */
public class FusioncloudRestoreService extends BaseFusioncloudService
        implements RestoreService, Rollbackable {

    public FusioncloudRestoreService(String configFilePath, String instanceId, String generation, String localhost, String taskName, String logId)
            throws LoadConfigurationException {
        super(configFilePath, instanceId, generation, localhost, taskName, logId);
    }

    @Override
    public ServiceReturn restore(ServiceParam serviceParam) throws BaseException {
        RestoreParam param = (RestoreParam) serviceParam;

        try {
            markStarted("初始化恢复服务");
            initRestoreService(param.getInstanceId(), param.getGenerationNum());
            markFinished("初始化恢复服务");

            markStarted("查询当前虚拟机信息");
            queryCurInstanceDetail(param.getInstanceId());
            markFinished("查询当前虚拟机信息");

            markStarted("卸载当前虚拟机所有硬盘");
            umountAllVols(param.getInstanceId());
            markFinished("卸载当前虚拟机所有硬盘");

            markStarted("创建空白硬盘");
            createEmptyVolumes();
            markFinished("创建空白硬盘");

            markStarted("挂载空白硬盘");
            mountNewVols(param.getInstanceId());
            markFinished("挂载空白硬盘");

            markStarted("建立rbd镜像索引");
            initImageSpecs();
            markFinished("建立rbd镜像索引");

            markStarted("创建恢复数据环境");
            buildRecorveryContext(param.getInstanceId(), param.getGenerationNum());
            markFinished("创建恢复数据环境");

            markStarted("初始化资源导入超时阈值");
            initResTimeoutLimit();
            markFinished("初始化资源导入超时阈值");

            markStarted("导入数据");
            importData(param.getInstanceId());
            markFinished("导入数据");

            markStarted("删除过期硬盘");
            deleteExpiredVolumes();
            markFinished("删除过期硬盘");

            markStarted("恢复备份副本的网络配置");
            recorveryInstanceNetwork(param.getInstanceId());
            markFinished("恢复备份副本的网络配置");

            markStarted("初始化虚拟机电源");
            powerOn(param.getInstanceId(), param.getPowerOn());
            markFinished("初始化虚拟机电源");

            markStarted("恢复完毕");
            finalizeRestoreService();
            markFinished("恢复完毕");

        } catch (BaseException e) {
            markError(e.getMessage(), e.getCode());
            throw e;
        }
        return serviceReturn();
    }

    /**
     * 待恢复的实例模型
     */
    private InstanceModel model2Restore;

    /**
     * 初始化恢复服务
     */
    @Procedure
    private void initRestoreService(String instanceId, String generationNum)
            throws LocalReadException, NoAvailableAgentException, LocalMakeDirectoryException, QueryPhysicalHostException {
        //加载待恢复的实例模型
        String modelStr = LocalAccess.os().read(this.getInstanceModelFile(instanceId, generationNum));
        logger.info("等待恢复的实例模型为：{}",modelStr);

        Gson gson = new Gson();
        this.model2Restore = gson.fromJson(modelStr,InstanceModel.class);

        //创建临时目录
        String tempDir = this.getTemporaryContentDir(instanceId);
        logger.info("get tempDir为：{}",tempDir);
        LocalAccess.os().mkdir(tempDir);
        //挂载nfs
        this.initAgentPool();
    }

    /**
     * 当前实例模型
     */
    private NovaServer curInstanceDetail;

    /**
     * 查询当前实例模型
     */
    @Procedure
    private void queryCurInstanceDetail(String instanceId) throws QueryInstanceException {
        this.curInstanceDetail = this.access.getInstanceDetail(instanceId);
    }

    /**
     * 卸载所有硬盘
     */
    @Procedure
    private void umountAllVols(String instanceId) throws QueryVolumeException,
            DetachVolumeException, DetachVolumeTimeoutException {

        for (String volumeId : this.curInstanceDetail.getOsExtendedVolumesAttached()) {
            Volume detailVolume = this.access.getVolumeDetail(volumeId);
            //卸载所有非系统硬盘
            if(!detailVolume.bootable()){
                this.access.detachVolume(instanceId,volumeId);
                logger.info("server :{} success deatach volume {} ",instanceId,volumeId);
            }
        }
    }

    //新旧磁盘映射
    private Map<String, String> volOld2New = new HashMap<>();

    /**
     * 创建与待恢复实例相同规格的硬盘
     */
    @Procedure
    private void createEmptyVolumes() throws
            CreateVolumeException, QueryVolumeException, CreateVolumeTimeoutException {

        for (DetailVolumeResponse volumeResponse : this.model2Restore.getVolumeDetailList()) {
            boolean bootable = Optional.ofNullable(volumeResponse.getBootable()).orElse(false);
            if(!bootable){
                String newVolId = this.access.createEmptyVolume(
                        volumeResponse.getSize(),
                        volumeResponse.getVolumeType(),
                        volumeResponse.getName(),
                        false
                );
                volOld2New.put(volumeResponse.getId(), newVolId);
            }else{
                volOld2New.put(volumeResponse.getId(), volumeResponse.getId());
            }
        }
    }

    /**
     * 挂载新创建的空白卷
     */
    @Procedure
    private void mountNewVols(String instanceId)
            throws AttachVolumeException, QueryVolumeException, AttachVolumeTimeoutException {
        for (DetailVolumeResponse volumeResponse : this.model2Restore.getVolumeDetailList()) {
            boolean bootable = Optional.ofNullable(volumeResponse.getBootable()).orElse(false);
            if(!bootable){
                String volId = volumeResponse.getId();
                String device = volumeResponse.getAttachments().get(0).getDevice();
                logger.info("begin to mountNewVols instanceId:{},volId:{},device:{}",instanceId,volId,device);
                this.access.attachVolume(instanceId,this.volOld2New.get(volId),device);
            }
        }
    }

    /**
     * 资源id与对应的镜像spec
     */
    private Map<String, String> src2ImageSpec = new HashMap<>();

    /**
     * 初始化资源id到rbd镜像spec的映射
     */
    @Procedure
    private void initImageSpecs() {
        for (Map.Entry<String, String> entry : this.volOld2New.entrySet()) {
            String srcId = entry.getKey();
            String newVolId = entry.getValue();
            logger.info("srcId为:{},newVolId为：{}",srcId,newVolId);

            String poolName = "volumes";
            logger.info("poolname为：{}",poolName);
            String imageSpec = this.getImageSpec(poolName, newVolId);
            logger.info("imageSpec：{}",imageSpec);
            src2ImageSpec.put(srcId, imageSpec);
        }
    }

    /**
     * 创建临时目录，并在临时目录中创建持久化镜像的副本快照
     */
    @Procedure
    private void buildRecorveryContext(String instanceId, String generationNum)
            throws LocalSnapshotException {
        String tempDir = this.getTemporaryContentDir(instanceId);
        String persistentDir = this.getPersistentContentDir(instanceId, generationNum);
        for (String resId : this.src2ImageSpec.keySet()) {
            String src = Paths.get(persistentDir.substring(mdfsRoot.length()), resId).toString();
            String dest = Paths.get(tempDir.substring(mdfsRoot.length()), resId).toString();
            LocalAccess.mdfs().snapshot(src, dest);
        }
    }

    /**
     * 记录备份资源的超时限制
     */
    private Map<String, Integer> resTimeoutLimit = new HashMap<>();

    /**
     * 初始化资源导入超时阈值
     */
    @Procedure
    private void initResTimeoutLimit() {
        for (DetailVolumeResponse detailVolumeResponse : this.model2Restore.getVolumeDetailList()) {
            int coe = detailVolumeResponse.getSize();
            int timeout = 10 * coe * coe + 600;
            if (timeout < 0 || timeout > 3600 * 24) {
                timeout = 3600 * 24;
            }
            resTimeoutLimit.put(detailVolumeResponse.getId(), timeout);
        }
    }

    /**
     * 恢复虚拟机的数据，将本地持久化的镜像导入到rbd块设备中
     */
    @Procedure
    private void importData(String instanceId)
            throws AgentStatusErrorException, RpcRbdImportException, AsyncTaskTimeoutException {
        String mountPoint = this.getMountpoint();
        String ioRecordFile = this.getIORecordFile(instanceId, taskUUID);
        for (Map.Entry<String, String> entry : this.src2ImageSpec.entrySet()) {
            String resId = entry.getKey();
            String fromFile = Paths.get(mountPoint, resId).toString();
            String imageSpec = entry.getValue();
            String host = this.getAvailableHost();
            logger.info("radosUserId{},cephConf为{}，imageSpec为{}，timelimit为{},ioRecordFile为{}",
                    radosUserId,cephConf,imageSpec,resTimeoutLimit.get(resId),ioRecordFile);
            RemoteAccess.rados(host).rbdImportImageWithIOMonitor(
                    radosUserId,
                    cephConf,
                    fromFile,
                    imageSpec,
                    this.model2Restore.getVolDiffBitmap().get(resId),
                    resTimeoutLimit.get(resId),
                    ioRecordFile
            );
        }
    }

    /**
     * 删除过期磁盘
     */
    @Procedure
    private void deleteExpiredVolumes() throws QueryVolumeException, DeleteVolumeException {

        for (String volumeId : this.curInstanceDetail.getOsExtendedVolumesAttached()) {
            CinderVolume detailVolume = this.access.getVolumeDetail(volumeId);
            if (!detailVolume.bootable()) {
                this.access.deleteVolume(volumeId);
            }
        }
    }

    /**
     * 恢复虚拟机的网卡
     * 01：删除当前网卡
     * 02：创建新的网卡（以之前的ipaddr为基准）
     * 03：
     */
    @Procedure
    private void recorveryInstanceNetwork(String instanceId) throws  QueryPortException {

        //  删除当前网卡
        List<String> portIds = getCurInstancePortId();
        portIds.forEach(portId -> {
            try {
                this.access.detachNIC(instanceId, portId);
            } catch (DetachNICException e) {
                logger.error(e.getMessage());
            }
        });

        this.model2Restore.getPorts().forEach(detailPortResponse -> {
            String networkId = detailPortResponse.getNetworkId();
            String nicName = detailPortResponse.getName();
            String newPortId = "";
            try {
                newPortId = this.access.createNIC(networkId ,nicName);
                this.access.attachNIC(instanceId, newPortId);
                logger.info("instance id:{} attach NIC id:{}",instanceId,networkId);
            } catch (CreateNICException e) {
                logger.error("新建port id为：{}时发生错误{}", newPortId, e.getMessage());
            } catch (AttachNICException e) {
                logger.error("实例:{},绑定网卡:{}时发生错误:{}", instanceId, newPortId, e.getMessage());
            }
        });

    }

    /**
     * 获取当前网卡 id集合
     * @return
     * @throws QueryPortException
     */
    private List<String> getCurInstancePortId() throws QueryPortException {
        Map<String, List<? extends Address>> addressesMap = this.curInstanceDetail.getAddresses().getAddresses();
        List<String> ips = new ArrayList<>();
        addressesMap.forEach((key, value) -> {
            String ip = value.stream().map(Address::getAddr).collect(Collectors.toList()).get(0);
            ips.add(ip);
        });

        return this.access.getPortList().stream()
                .filter((Port port) -> {
                    String portIp = port.getFixedIps().stream()
                            .map(IP::getIpAddress)
                            .collect(Collectors.toList())
                            .get(0);
                    return ips.contains(portIp);
                })
                .map(Port::getId)
                .collect(Collectors.toList());
    }

    /**
     * 虚拟机开机
     */
    @Procedure
    private void powerOn(String instanceId, boolean shouldPowerOn) {
        if (shouldPowerOn) {
            try {
                this.access.powerOnInstance(instanceId);
            } catch (BaseException e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Procedure
    private void finalizeRestoreService() {
        this.dischargeAgentPool();
    }

    private RestoreReturn serviceReturn() {
        return new RestoreReturn();
    }

    @Override
    public void rollBack(ServiceParam serviceParam)  {
        //如果已经创建了空白卷，应该删除
        this.dischargeAgentPool();
    }
}
