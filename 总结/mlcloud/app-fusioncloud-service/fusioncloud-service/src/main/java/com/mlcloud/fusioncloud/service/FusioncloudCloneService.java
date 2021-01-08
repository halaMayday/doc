package com.mlcloud.fusioncloud.service;


import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.exception.LoadConfigurationException;
import com.mlcloud.defination.exception.NoAvailableAgentException;
import com.mlcloud.defination.service.CloneService;
import com.mlcloud.defination.service.Rollbackable;
import com.mlcloud.fusioncloud.bean.DetailVolumeResponse;
import com.mlcloud.fusioncloud.bean.InstanceModel;
import com.mlcloud.fusioncloud.bean.service.accept.CloneParam;
import com.mlcloud.fusioncloud.bean.service.ret.CloneReturn;
import com.mlcloud.fusioncloud.exception.*;
import com.mlcloud.local.LocalAccess;
import com.mlcloud.local.exception.mdfs.LocalSnapshotException;
import com.mlcloud.local.exception.os.LocalMakeDirectoryException;
import com.mlcloud.local.exception.os.LocalReadException;
import com.mlcloud.rpc.client.RemoteAccess;
import com.mlcloud.rpc.client.exception.AgentStatusErrorException;
import com.mlcloud.rpc.client.exception.AsyncTaskTimeoutException;
import com.mlcloud.rpc.client.exception.rados.RpcRbdImportException;
import org.apache.commons.lang3.StringUtils;
import org.openstack4j.model.common.BasicResource;
import org.openstack4j.model.common.IdEntity;
import org.openstack4j.model.compute.SecurityGroup;
import org.openstack4j.model.network.State;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：hf
 * @date ：Created in 2020/11/25 5:45 下午
 * @description：
 */
public class FusioncloudCloneService extends BaseFusioncloudService implements CloneService, Rollbackable {

    public FusioncloudCloneService(String configFilePath, String instanceId, String generation, String localhost, String taskName, String logId)
            throws LoadConfigurationException {
        super(configFilePath, instanceId, generation, localhost, taskName, logId);
    }

    @Override
    public ServiceReturn cloneInstance(ServiceParam serviceParam) throws BaseException {
        CloneParam param = (CloneParam) serviceParam;
        try {
            markStarted("初始化克隆服务");
            initCloneService(param.getSrcInstanceId(), param.getGenerationNum());
            markFinished("初始化克隆服务");

            markStarted("创建空白卷");
            createVolumes();
            markFinished("创建空白卷");

            markStarted("配置网络");
            cloneNetwork(param.getNetworkAccess());
            markFinished("配置网络");

            markStarted("创建空白虚拟机");
            createInstance(param.getDestInstancceName());
            markFinished("创建空白虚拟机");

            markStarted("挂载非系统空白卷");
            mountVolumes();
            markFinished("挂载非系统空白卷");

            markStarted("关闭虚拟机");
            powerOff();
            markFinished("关闭虚拟机");

            markStarted("建立rbd镜像索引");
            initImageSpecs();
            markFinished("建立rbd镜像索引");

            markStarted("创建克隆数据环境");
            buildCloneContext(param.getSrcInstanceId(), param.getGenerationNum());
            markFinished("创建克隆数据环境");

            markStarted("初始化资源导入超时阈值");
            initResTimeoutLimit();
            markFinished("初始化资源导入超时阈值");

            markStarted("导入数据");
            importData(param.getSrcInstanceId());
            markFinished("导入数据");

            markStarted("初始化虚拟机电源");
            powerOn(param.getPowerOn());
            markFinished("初始化虚拟机电源");

            markStarted("克隆完毕");
            finalizeCloneService();
            markFinished("克隆完毕");
        } catch (BaseException e) {
            markError(e.getMessage(), e.getCode());
            throw e;
        }
        return serviceReturn();


    }

    /**
     * 待克隆的实例模型
     */
    private InstanceModel model2Clone;

    /**
     * 初始化克隆服务
     */
    @Procedure
    private void initCloneService(String instanceId, String generationNum)
            throws LocalReadException, NoAvailableAgentException, LocalMakeDirectoryException, QueryPhysicalHostException {
        //从世代信息文件中加载克隆机器模型
        String modelStr = LocalAccess.os().read(this.getInstanceModelFile(instanceId, generationNum));
        Gson gson = new Gson();
        this.model2Clone = gson.fromJson(modelStr, InstanceModel.class);
        //创建临时目录
        String tempDir = this.getTemporaryContentDir(instanceId);
        LocalAccess.os().mkdir(tempDir);
        //挂载nfs
        this.initAgentPool();
    }

    /**
     * 新旧硬盘的id映射
     */
    private Map<String, String> volOld2New = new HashMap<>();

    /**
     * 网络id
     */
    List<String> networkIds = new ArrayList<>();

    /**
     * 创建空白volume
     */
    @Procedure
    private void createVolumes() throws
            CreateVolumeException, QueryVolumeException, CreateVolumeTimeoutException {

        for (DetailVolumeResponse detailVolumeResponse : this.model2Clone.getVolumeDetailList()) {
            boolean bootable = Optional.ofNullable(detailVolumeResponse.getBootable()).orElse(false);
            String newVolId = this.access.createEmptyVolume(
                    detailVolumeResponse.getSize(),
                    detailVolumeResponse.getVolumeType(),
                    detailVolumeResponse.getName(),
                    bootable
            );
            if(bootable){
                cloneSysVolumeId = newVolId;
            }
            volOld2New.put(detailVolumeResponse.getId(), newVolId);
        }
    }
    /**
     *  克隆网络配置
     *
     */
    @Procedure
    private void cloneNetwork(boolean accessNetwork) throws QueryNetworkException {
        Map<String, String> availableNetworkMap = this.access.getNetworkList().stream()
                .filter(network -> network.getStatus().equals(State.ACTIVE)).collect(Collectors
                        .toMap(BasicResource::getName, IdEntity::getId,
                                (oldVal, currVal) -> currVal));
        logger.info("query available network {}", availableNetworkMap);

        if (accessNetwork) {
             networkIds = availableNetworkMap.entrySet().stream()
                    .filter(v -> this.model2Clone.getDetail().getAddresses().getAddresses().containsKey(v.getKey()))
                    .map(Map.Entry::getValue).collect(Collectors.toList());

            if (networkIds.isEmpty()) {
                //过滤出ACIVE的网络，然后选择第一个作为克隆机的创建网络
                String defaultNetId = availableNetworkMap.values().stream().findFirst().orElse("");
                networkIds = Lists.newArrayList(defaultNetId);
            }
        } else {
            //过滤出ACIVE的网络，然后选择第一个作为克隆机的创建网络
            String defaultNetId = availableNetworkMap.values().stream().findFirst().orElse("");
            networkIds = Lists.newArrayList(defaultNetId);
        }

    }

    private String cloneInstanceId ;

    /**
     * 克隆实例系统卷id
     */
    private String cloneSysVolumeId;

    /**
     * 创建空白虚拟机
     */
    @Procedure
    private void createInstance(String instanceName) throws CreateInstanceException,
            CreateInstanceTimeoutException, QueryInstanceException, QueryVolumeException {
        cloneSysVolumeId = findSysVolumeId();

        logger.info("cloneSysVolumeId {}", cloneSysVolumeId);
        String flavorId = this.model2Clone.getDetail().getFlavorId();
        String cloneInstanceName = instanceName.length() > 32 ? instanceName.substring(0, 29) + "..." : instanceName;
        String defaultPassword = "1qaz@WSX";
        String availabilityZone = this.model2Clone.getDetail().getAvailabilityZone();
        //安全组信息
        List<String> securityGroups = this.model2Clone.getDetail().getSecurityGroups().stream()
                .map(SecurityGroup::getName)
                .collect(Collectors.toList());

        cloneInstanceId = this.access.createInstance(
                cloneInstanceName,
                flavorId,
                defaultPassword,
                networkIds,
                availabilityZone,
                cloneSysVolumeId,
                securityGroups);
    }

    private String findSysVolumeId() {
        List<DetailVolumeResponse> list = this.model2Clone.getVolumeDetailList().stream()
                .filter(DetailVolumeResponse::getBootable)
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return this.volOld2New.get(list.get(0).getId());
        }

        return  list.stream().filter(detailVolumeResponse -> detailVolumeResponse.getAttachments().stream()
                .anyMatch(cinderVolumeAttachment -> "/dev/vda".equalsIgnoreCase(cinderVolumeAttachment.getDevice())))
                .map(DetailVolumeResponse::getId).findFirst().orElse("");
    }

    /**
     * 挂载空白非系统卷
     */
    @Procedure
    private void mountVolumes() throws
            AttachVolumeException, QueryVolumeException, AttachVolumeTimeoutException {

        for (DetailVolumeResponse oldVol : this.model2Clone.getVolumeDetailList()) {
            boolean bootable = Optional.ofNullable(oldVol.getBootable()).orElse(false);
            if (!bootable) {
                String volId = this.volOld2New.get(oldVol.getId());
                String device = oldVol.getAttachments().get(0).getDevice();
                logger.info("serverId:{} prepare to mount volume :{},device ={} ", this.cloneInstanceId, volId, device);
                this.access.attachVolume(this.cloneInstanceId, volId, device);
            }
        }
    }

    /**
     * 关机
     */
    @Procedure
    private void powerOff()
            throws PowerOffInstanceException, PowerOffTimeoutException, QueryInstanceException {
        this.access.powerOffInstance(cloneInstanceId);
    }

    /**
     * 记录sourceId到需要导入数据的rbd镜像的关系
     */
    private Map<String, String> src2ImageSpec = new HashMap<>();

    /**
     * 初始化源资源到所导入的rbd镜像定义符的映射
     */
    @Procedure
    private void initImageSpecs() {
        for (Map.Entry<String, String> entry : this.volOld2New.entrySet()) {
            String srcId = entry.getKey();
            String newVolId = entry.getValue();
            String poolName = "volumes";
            String imageSpec = this.getImageSpec(poolName, newVolId);
            this.src2ImageSpec.put(srcId, imageSpec);
        }
    }

    /**
     * 创建临时目录，并在临时目录中创建持久化镜像的副本快照
     */
    @Procedure
    private void buildCloneContext(String instanceId, String generationNum)
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
        for (DetailVolumeResponse volumeResponse : this.model2Clone.getVolumeDetailList()) {
            int coe = volumeResponse.getSize();
            int timeout = 10 * coe * coe + 600;
            if (timeout < 0 || timeout > 3600 * 24) {
                timeout = 3600 * 24;
            }
            resTimeoutLimit.put(volumeResponse.getId(), timeout);
        }
    }

    /**
     * 导入克隆数据，将本地持久化的镜像导入到rbd块设备中
     */
    @Procedure
    private void importData(String instanceId)
            throws AgentStatusErrorException, RpcRbdImportException, AsyncTaskTimeoutException {
        String mountPoint = this.getMountpoint();
        String ioRecordFile = this.getIORecordFile(instanceId, taskUUID);

        for (Map.Entry<String, String> entry : this.src2ImageSpec.entrySet()) {
            String srcId = entry.getKey();
            String fromFile = Paths.get(mountPoint, srcId).toString();
            String imageSpec = entry.getValue();
            String host = this.getAvailableHost();
            RemoteAccess.rados(host).rbdImportImageWithIOMonitor(
                    radosUserId,
                    cephConf,
                    fromFile,
                    imageSpec,
                    this.model2Clone.getVolDiffBitmap().get(srcId),
                    this.resTimeoutLimit.get(srcId),
                    ioRecordFile
            );
        }
    }

    /**
     * 开机
     */
    @Procedure
    private void powerOn(boolean isPowerOn) {
        if (isPowerOn) {
            try {
                this.access.powerOnInstance(this.cloneInstanceId);
            } catch (BaseException e) {
                logger.error(e.getMessage());
            }
        }
    }


    /**
     * 结束克隆服务
     */
    @Procedure
    private void finalizeCloneService() {
        this.dischargeAgentPool();
    }

    private CloneReturn serviceReturn() {
        return new CloneReturn();
    }

    @Override
    public void rollBack(ServiceParam serviceParam) throws BaseException {
        logger.info("START ROLLBACKUP");

        //删除克隆实例
        try {
            if(StringUtils.isNotBlank(this.cloneInstanceId)){
                this.access.deleteInstance(this.cloneInstanceId);
                logger.info("DELETED CLONE INSTANCE: {}", this.cloneInstanceId);
            }
        } catch (DeleteInstanceException e) {
            logger.error("DELETED CLONE INSTANCE: {} FAILED,ERR_MSG:{}",this.cloneInstanceId,e.getMessage());
        }
        //删除克隆卷
        String copyVolumeId = null;
        try {
            for (Map.Entry<String, String> entry : this.volOld2New.entrySet()) {
                copyVolumeId = entry.getValue();
                this.access.deleteVolume(entry.getValue());
            }
        } catch (DeleteVolumeException e) {
            logger.error("DELETED CLONE VOLUME: {} FAILED,ERR_MSG:{}",copyVolumeId,e.getMessage());
        }

        //挂载nfs
        this.dischargeAgentPool();
    }
}
