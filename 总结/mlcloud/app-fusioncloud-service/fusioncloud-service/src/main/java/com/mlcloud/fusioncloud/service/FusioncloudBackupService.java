package com.mlcloud.fusioncloud.service;

import com.google.gson.Gson;
import com.mlcloud.common.bean.Pair;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.bean.service.GenerationInfoBean;
import com.mlcloud.defination.exception.LoadConfigurationException;
import com.mlcloud.defination.exception.NoAvailableAgentException;
import com.mlcloud.defination.service.BackupService;
import com.mlcloud.defination.service.Rollbackable;
import com.mlcloud.fusioncloud.bean.DetailInstanceResponse;
import com.mlcloud.fusioncloud.bean.DetailPortResponse;
import com.mlcloud.fusioncloud.bean.DetailVolumeResponse;
import com.mlcloud.fusioncloud.bean.InstanceModel;
import com.mlcloud.fusioncloud.bean.service.accept.BackupParam;
import com.mlcloud.fusioncloud.bean.service.ret.BackupReturn;
import com.mlcloud.fusioncloud.exception.*;
import com.mlcloud.local.LocalAccess;
import com.mlcloud.local.exception.mdfs.LocalSnapshotException;
import com.mlcloud.local.exception.os.*;
import com.mlcloud.rpc.client.RemoteAccess;
import com.mlcloud.rpc.client.exception.AgentStatusErrorException;
import com.mlcloud.rpc.client.exception.AsyncTaskTimeoutException;
import com.mlcloud.rpc.client.exception.rados.RpcRbdExportException;
import com.mlcloud.rpc.client.exception.rados.RpcRbdExportImageDiffException;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.network.IP;
import org.openstack4j.openstack.compute.domain.NovaAddresses;
import org.openstack4j.openstack.compute.domain.NovaSecurityGroup;
import org.openstack4j.openstack.compute.domain.NovaServer;
import org.openstack4j.openstack.networking.domain.NeutronIP;
import org.openstack4j.openstack.networking.domain.NeutronPort;
import org.openstack4j.openstack.storage.block.domain.CinderVolume;
import org.openstack4j.openstack.storage.block.domain.CinderVolumeAttachment;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：hf
 * @date ：Created in 2020/11/25 5:36 下午
 * @modified By：
 * @version: $
 */
public class FusioncloudBackupService extends BaseFusioncloudService implements BackupService, Rollbackable {

    public FusioncloudBackupService(String configFilePath, String instanceId, String generation, String localhost, String taskName, String logId)
            throws LoadConfigurationException {
        super(configFilePath, instanceId, generation, localhost, taskName, logId);
    }

    @Override
    public ServiceReturn backup(ServiceParam serviceParam) throws BaseException {
        BackupParam param = (BackupParam) serviceParam;

        try {
            long startTimestamp = System.currentTimeMillis();

            markStarted("检查备份前置条件");
            checkPrerequisite(param);
            markFinished("检查备份前置条件");

            markStarted("初始化备份服务");
            initBackupService(param.getInstanceId(), param.getGenerationNum());
            markFinished("初始化备份服务");

            markStarted("查询虚拟机详情");
            queryInstanceDetail(param.getInstanceId());
            markFinished("查询虚拟机详情");

            markStarted("建立rbd镜像定义符的索引");
            initImageSpecs();
            markFinished("建立rbd镜像定义符的索引");

            markStarted("创建快照");
            createSnapshots();
            markFinished("创建快照");

            markStarted("初始化数据导出类型(全量/增量)");
            initExportTypes(param.getIsFull());
            markFinished("初始化数据导出类型(全量/增量)");

            markStarted("初始化资源导出超时阈值");
            initResTimeoutLimit();
            markFinished("初始化资源导出超时阈值");

            markStarted("创建备份数据环境");
            buildBackupContext(param.getInstanceId());
            markFinished("创建备份数据环境");

            markStarted("导出数据");
            exportImages(param.getInstanceId());
            markFinished("导出数据");

            markStarted("持久化镜像数据");
            persistentBackupImages(param.getInstanceId(), param.getGenerationNum());
            markFinished("持久化镜像数据");

            markStarted("记录备份实例详情");
            recordBackupModel(param.getInstanceId(), param.getGenerationNum());
            markFinished("记录备份实例详情");

            markStarted("删除过期快照");
            removeExpiredSnaps();
            markFinished("删除过期快照");

            markStarted("备份完毕");
            finalizeBackupService(param.getInstanceId(), param.getGenerationNum());
            markFinished("备份完毕");

            return serviceReturn(param, System.currentTimeMillis() - startTimestamp);
        } catch (BaseException e) {
            markError(e.getMessage(), e.getCode());
            throw e;
        }
    }

    /**
     * 世代信息
     */
    private GenerationInfoBean generationInfo;

    /**
     * 检查备份前置条件
     */
    @Procedure
    private void checkPrerequisite(BackupParam param) throws LocalReadException, LocalWriteException {
        String instanceId = param.getInstanceId();
        String generationInfoFile = this.getGenerationFile(instanceId);
        Gson gson = new Gson();
        if (!LocalAccess.os().fileExist(generationInfoFile)) {
            param.setIsFull(true);
        } else {
            String content = LocalAccess.os().read(generationInfoFile);
            if (content.isEmpty()) {
                param.setIsFull(true);
            } else {
                this.generationInfo = gson.fromJson(content, GenerationInfoBean.class);
                String dependentGen = this.generationInfo.getLastSuccessGen();
                String dependentDir = this.getPersistentContentDir(instanceId, dependentGen);
                if (!LocalAccess.os().dirExist(dependentDir)) {
                    param.setIsFull(true);
                }
            }
        }
        if (param.getIsFull().equals(Boolean.TRUE)) {
            this.generationInfo = new GenerationInfoBean();
            this.generationInfo.setResId2snap(new HashMap<>());
        }
        this.generationInfo.setCurGen(param.getGenerationNum());
        String infoFile = this.getGenerationFile(instanceId);

        LocalAccess.os().write(gson.toJson(this.generationInfo), infoFile);
    }

    /**
     * 初始化备份服务
     */
    @Procedure
    private void initBackupService(String instanceId, String generationNum)
            throws LocalMakeDirectoryException, NoAvailableAgentException, QueryPhysicalHostException {

        String persistentDir = this.getPersistentContentDir(instanceId, generationNum);
        String temporaryDir = this.getTemporaryContentDir(instanceId);
        if (!LocalAccess.os().dirExist(persistentDir)) {
            LocalAccess.os().mkdir(persistentDir);
        }
        if (!LocalAccess.os().dirExist(temporaryDir)) {
            LocalAccess.os().mkdir(temporaryDir);
        }
        //挂载nfs
        this.initAgentPool();
    }

    /**
     * instanced的详情模型
     */
    private InstanceModel instanceModel = new InstanceModel();

    /**
     * 查询实例详情
     */
    @Procedure
    private void queryInstanceDetail(String instanceId)
            throws QueryInstanceException, QueryVolumeException, QueryPortException {
        NovaServer server = this.access.getInstanceDetail(instanceId);
        //查询，拼装实例信息
        DetailInstanceResponse instanceResponse = copyServerProperties(server);
        //查询，拼装volume信息
        List<DetailVolumeResponse> volumeDetailList = copyVolumeProperties(server);

        instanceModel.setDetail(instanceResponse);
        instanceModel.setVolumeDetailList(volumeDetailList);
        instanceModel.setVolDiffBitmap(new HashMap<>());

        List<DetailPortResponse> detailPorts = copyPortProperties(server);
        instanceModel.setPorts(detailPorts);

    }

    /**
     * 查询实例详情，只截取有用的字段
     */
    private DetailInstanceResponse copyServerProperties(NovaServer server){
        return  DetailInstanceResponse.builder().id(server.getId())
                .name(server.getName())
                .tenantId(server.getTenantId())
                .host(server.getHost())
                .hostId(server.getHostId())
                .availabilityZone(server.getAvailabilityZone())
                .imageId(server.getImageId())
                .status(server.getStatus())
                .powerState(server.getPowerState())
                .osExtendedVolumesAttached(server.getOsExtendedVolumesAttached())
                .securityGroups((List<NovaSecurityGroup>) server.getSecurityGroups())
                .flavorId(server.getFlavorId())
                .addresses((NovaAddresses) server.getAddresses())
                .build();
    }

    /**
     * 查询volume详情，只截取有用的字段
     * poolname目前固定写死，暂时没想到更好的处理办法
     */
    private List<DetailVolumeResponse>copyVolumeProperties(NovaServer server) throws QueryVolumeException {
        List<DetailVolumeResponse> volumeDetailList = new ArrayList<>();

        for (String volumeId : server.getOsExtendedVolumesAttached()) {
            CinderVolume volume = this.access.getVolumeDetail(volumeId);
            DetailVolumeResponse volumeResponse = DetailVolumeResponse.builder().id(volumeId)
                    .volumeType(volume.getVolumeType())
                    .size(volume.getSize())
                    .name(volume.getName())
                    .status(volume.getStatus())
                    .attachments((List<CinderVolumeAttachment>) volume.getAttachments())
                    .bootable(volume.bootable())
                    .poolName("volumes")
                    .build();
            volumeDetailList.add(volumeResponse);
        }
        return volumeDetailList;
    }
    /**
     * 查询port网络端口信息
     * 没有采用浅拷贝
     */
    private List<DetailPortResponse> copyPortProperties(NovaServer server) throws QueryPortException {
        //查询所有的网卡信息
        List<NeutronPort> portLists = this.access.getPortList();
        //实例的ip集合
        List<String> ips = new ArrayList<>();
        server.getAddresses().getAddresses().forEach((key, value) -> {
            String ip = value.stream().map(Address::getAddr).collect(Collectors.toList()).get(0);
            ips.add(ip);
        });
        List<DetailPortResponse> list = new ArrayList<>();
        //循环 网卡列表，筛选出该server的网卡信息 ：need optimization
        portLists.forEach(port -> {
            String ipAddr= port.getFixedIps().stream().map(IP::getIpAddress).collect(Collectors.toList()).get(0);
            if (ips.contains(ipAddr)) {
                DetailPortResponse portResponse = DetailPortResponse.builder().id(port.getId())
                        .name(port.getName())
                        .networkId(port.getNetworkId())
                        .fixedIps((Set<NeutronIP>) port.getFixedIps())
                        .state(port.getState())
                        .build();
                list.add(portResponse);
            }
        });
        return  list;
    }

    /**
     * 资源id对应的rbd设备spec
     */
    private Map<String, String> resId2ImageSpec = new HashMap<>();

    /**
     * 资源id列表
     */
    private List<String> resIds = new ArrayList<>();

    /**
     * 初始化rbd镜像spec映射
     */
    @Procedure
    private void initImageSpecs() {
        List<DetailVolumeResponse> volumeDetailList = this.instanceModel.getVolumeDetailList();
        volumeDetailList.forEach(volumeDetail -> {
            String volumeId = volumeDetail.getId();
            String poolName = volumeDetail.getPoolName();
            String imageSpec = this.getImageSpec(poolName, volumeDetail.getId());
            this.resId2ImageSpec.put(volumeId, imageSpec);
            this.resIds.add(volumeId);
        });
    }

    /**
     * 资源id与对应新建snapshot的映射
     */
    private Map<String, String> resId2SnapId = new HashMap<>();

    /**
     * 创建快照
     */
    @Procedure
    private void createSnapshots()
            throws CreateSnapshotException, CreateSnapshotTimeoutException, QuerySnapshotException {
        for (String resId : this.resIds) {
            String snapId = createSnapshot(resId);
            resId2SnapId.put(resId, snapId);
        }
    }

    private String createSnapshot(String resId)
            throws CreateSnapshotException, CreateSnapshotTimeoutException, QuerySnapshotException {
        String snapshotName = getSnapshotName();
        return this.access.createSnapshot(resId, snapshotName);
    }

    /**
     * 记录所备份的资源执行的备份类型
     */
    private Map<String, Boolean> isFullBackup = new HashMap<>();

    /**
     * 确定资源的备份类型
     */
    @Procedure
    private void initExportTypes(boolean isFullByDefault) {
        for (String resId : this.resIds) {
            boolean hasOverlap = this.generationInfo.getResId2snap().containsKey(resId);
            isFullBackup.put(resId, isFullByDefault || !hasOverlap);
        }
    }

    /**
     * 记录备份资源的超时限制
     */
    private Map<String, Integer> resTimeoutLimit = new HashMap<>();

    /**
     * 初始化导出数据的超时阈值
     */
    @Procedure
    private void initResTimeoutLimit() {
        //根据卷的大小，设置最大时间阈值
        List<DetailVolumeResponse> volumeDetailList = this.instanceModel.getVolumeDetailList();
        volumeDetailList.forEach(volumeDetail->{
            int coe = volumeDetail.getSize();
            int timeout = 10 * coe * coe + 600;
            if (timeout < 0 || timeout > 3600 * 24) {
                timeout = 3600 * 24;
            }
            resTimeoutLimit.put(volumeDetail.getId(), timeout);
        });

    }

    /**
     * 准备备份数据环境（创建数据接收副本）
     */
    @Procedure
    private void buildBackupContext(String instanceId)
            throws LocalSnapshotException, LocalTruncateException {
        for (String resId : this.resIds) {
            boolean isFull = this.isFullBackup.get(resId);
            //如果是全量备份则创建空白文件副本
            if (isFull) {
                long byteSize =  this.instanceModel.getVolumeDetailList().stream()
                        .filter(volume -> (resId).equals(volume.getId()))
                        .mapToInt(DetailVolumeResponse::getSize)
                        .sum() * 1024 * 1024 * 1024L;
                String tmpDir = this.getTemporaryContentDir(instanceId);
                String dest = Paths.get(tmpDir, resId).toString();
                LocalAccess.os().truncateFile(dest, byteSize);
            }
            //如果是增量备份则从持久化目录中获取依赖快照的mdfs副本
            else {
                String persistentDir = this.getPersistentContentDir(instanceId, this.generationInfo.getLastSuccessGen()).substring(mdfsRoot.length());
                String tmpDir = this.getTemporaryContentDir(instanceId).substring(mdfsRoot.length());
                String src = Paths.get(persistentDir, resId).toString();
                String dest = Paths.get(tmpDir, resId).toString();
                LocalAccess.mdfs().snapshot(src, dest);
            }
        }
    }

    /**
     * 导出备份数据
     */
    @Procedure
    private void exportImages(String instanceId)
            throws AgentStatusErrorException, RpcRbdExportException, AsyncTaskTimeoutException, RpcRbdExportImageDiffException {
        String mountPoint = this.getMountpoint();
        String ioRecordFile = this.getIORecordFile(instanceId, this.taskUUID);
        for (String resId : this.resIds) {
            String host = this.getAvailableHost();
            String outputFile = Paths.get(mountPoint, resId).toString();
            Set<Pair<Long, Integer>> bitmap = exportDiffBitmap(resId, host);
            exportImage(resId, host, bitmap, outputFile, ioRecordFile);
        }
    }

    /**
     * 导出差量位图
     */
    private Set<Pair<Long, Integer>> exportDiffBitmap(String resId, String fromHost)
            throws AgentStatusErrorException, RpcRbdExportImageDiffException {
        boolean isFull = this.isFullBackup.get(resId);
        String imageSpec = this.resId2ImageSpec.get(resId);
        String snapshotCephName = this.getSnapshotNameInCeph(this.resId2SnapId.get(resId));
        Set<Pair<Long, Integer>> bitmap = RemoteAccess.rados(fromHost).rbdExportImageDiff(
                radosUserId,
                cephConf,
                imageSpec,
                snapshotCephName
        );
        //记录全量位图
        this.instanceModel.getVolDiffBitmap().put(resId, bitmap);
        if (!isFull) {
            String fromSnap = this.getSnapshotNameInCeph(this.generationInfo.getResId2snap().get(resId));
            bitmap = RemoteAccess.rados(fromHost).rbdExportImageDiff(
                    radosUserId,
                    cephConf,
                    imageSpec,
                    snapshotCephName,
                    fromSnap
            );
        }
        return bitmap;
    }

    /**
     * 根据差量位图导出数据
     */
    private void exportImage(String resId, String fromHost, Set<Pair<Long, Integer>> bitmap, String outputFile, String ioRecordFile)
            throws AgentStatusErrorException, RpcRbdExportException, AsyncTaskTimeoutException {
        logger.info("RESOURCE ID: {}", resId);
        String imageSpec = this.resId2ImageSpec.get(resId);
        String snapshotCephName = this.getSnapshotNameInCeph(this.resId2SnapId.get(resId));
        int timeoutSec = resTimeoutLimit.get(resId);
        RemoteAccess.rados(fromHost).rbdExportSnapshotWithIOMonitor(
                radosUserId,
                cephConf,
                imageSpec,
                snapshotCephName,
                outputFile,
                bitmap,
                timeoutSec,
                ioRecordFile
        );
        logger.info("FINISHED RESOURCE ID: {}", resId);
    }

    /**
     * 数据持久化
     */
    @Procedure
    private void persistentBackupImages(String instanceId, String generationNum)
            throws LocalMoveException {
        String persistentDir = this.getPersistentContentDir(instanceId, generationNum);
        String tempDir = this.getTemporaryContentDir(instanceId);
        for (String resId : this.resIds) {
            String persistentFile = Paths.get(persistentDir, resId).toString();
            String tempFile = Paths.get(tempDir, resId).toString();
            LocalAccess.os().move(tempFile, persistentFile);
        }
    }

    /**
     * 记录实例详情
     */
    @Procedure
    private void recordBackupModel(String instanceId, String generationNum)
            throws LocalWriteException {
        String recordFile = this.getInstanceModelFile(instanceId, generationNum);
        Gson gson = new Gson();
        LocalAccess.os().write(gson.toJson(instanceModel), recordFile);
    }

    /**
     * 删除过期快照
     */
    @Procedure
    private void removeExpiredSnaps()
            throws DeleteSnapshotException {
        for (Map.Entry<String, String> entry : this.generationInfo.getResId2snap().entrySet()) {
            this.access.deleteSnapshot(entry.getValue());
        }
    }

    /**
     * 结束备份服务
     */
    @Procedure
    private void finalizeBackupService(String instanceId, String generationNum)
            throws LocalWriteException {
        //更新备份世代信息
        this.generationInfo.setLastSuccessGen(generationNum);
        this.generationInfo.setResId2snap(this.resId2SnapId);
        String infoFile = this.getGenerationFile(instanceId);
        Gson gson = new Gson();
        LocalAccess.os().write(gson.toJson(this.generationInfo), infoFile);
        //断开nfs
        this.dischargeAgentPool();

    }

    private BackupReturn serviceReturn(BackupParam param, long spend) {
        BackupReturn ret = new BackupReturn();
        ret.setGeneration(param.getGenerationNum());
        ret.setMoref(param.getInstanceId());
        ret.setTotalTime(Long.toString(spend));
        ret.setDisk(instanceModel.getVolumeDetailList().stream()
                .map(volume -> {
                    BackupReturn.BackupInfoBean imageInfo = new BackupReturn.BackupInfoBean();
                    imageInfo.setDiskid(volume.getId());
                    imageInfo.setSize(Long.toString(volume.getSize() * 1024 * 1024 * 1024L));
                    imageInfo.setStatus("0");
                    imageInfo.setMode(Boolean.toString(isFullBackup.get(volume.getId())));
                    imageInfo.setElapsedTimeMs(Long.toString(spend));
                    return imageInfo;
                }).collect(Collectors.toList()));
        return ret;
    }


    @Override
    public void rollBack(ServiceParam serviceParam) {
        logger.info("START ROLLBACKUP");
        BackupParam param = (BackupParam) serviceParam;
        for (Map.Entry<String, String> entry : resId2SnapId.entrySet()) {
            //删除平台上的快照
            try {
                this.access.deleteSnapshot(entry.getValue());
                logger.info("DELETED SNAPSHOT: {}", entry.getValue());
            }catch (BaseException e) {
                logger.error("{}: {}", e.getCode().getCode(), e.getCode().getMessage());
            }
            //删除持久化目录中的磁盘文件
            try {
                String persitentImageFile = Paths.get(
                        this.getPersistentContentDir(
                                param.getInstanceId(),
                                param.getGenerationNum()
                        ),
                        entry.getKey()
                ).toString();
                if (LocalAccess.os().fileExist(persitentImageFile)) {
                    LocalAccess.os().delete(persitentImageFile);
                    logger.info("DELETED FILE: {}", persitentImageFile);
                }
            } catch (BaseException e) {
                logger.error("{}: {}", e.getCode().getCode(), e.getCode().getMessage());
            }
        }
       // 断开nfs
        this.dischargeAgentPool();
    }
}
