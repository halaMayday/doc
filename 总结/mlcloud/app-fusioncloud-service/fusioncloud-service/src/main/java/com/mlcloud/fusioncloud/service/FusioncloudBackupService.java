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
 * @author ???hf
 * @date ???Created in 2020/11/25 5:36 ??????
 * @modified By???
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

            markStarted("????????????????????????");
            checkPrerequisite(param);
            markFinished("????????????????????????");

            markStarted("?????????????????????");
            initBackupService(param.getInstanceId(), param.getGenerationNum());
            markFinished("?????????????????????");

            markStarted("?????????????????????");
            queryInstanceDetail(param.getInstanceId());
            markFinished("?????????????????????");

            markStarted("??????rbd????????????????????????");
            initImageSpecs();
            markFinished("??????rbd????????????????????????");

            markStarted("????????????");
            createSnapshots();
            markFinished("????????????");

            markStarted("???????????????????????????(??????/??????)");
            initExportTypes(param.getIsFull());
            markFinished("???????????????????????????(??????/??????)");

            markStarted("?????????????????????????????????");
            initResTimeoutLimit();
            markFinished("?????????????????????????????????");

            markStarted("????????????????????????");
            buildBackupContext(param.getInstanceId());
            markFinished("????????????????????????");

            markStarted("????????????");
            exportImages(param.getInstanceId());
            markFinished("????????????");

            markStarted("?????????????????????");
            persistentBackupImages(param.getInstanceId(), param.getGenerationNum());
            markFinished("?????????????????????");

            markStarted("????????????????????????");
            recordBackupModel(param.getInstanceId(), param.getGenerationNum());
            markFinished("????????????????????????");

            markStarted("??????????????????");
            removeExpiredSnaps();
            markFinished("??????????????????");

            markStarted("????????????");
            finalizeBackupService(param.getInstanceId(), param.getGenerationNum());
            markFinished("????????????");

            return serviceReturn(param, System.currentTimeMillis() - startTimestamp);
        } catch (BaseException e) {
            markError(e.getMessage(), e.getCode());
            throw e;
        }
    }

    /**
     * ????????????
     */
    private GenerationInfoBean generationInfo;

    /**
     * ????????????????????????
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
     * ?????????????????????
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
        //??????nfs
        this.initAgentPool();
    }

    /**
     * instanced???????????????
     */
    private InstanceModel instanceModel = new InstanceModel();

    /**
     * ??????????????????
     */
    @Procedure
    private void queryInstanceDetail(String instanceId)
            throws QueryInstanceException, QueryVolumeException, QueryPortException {
        NovaServer server = this.access.getInstanceDetail(instanceId);
        //???????????????????????????
        DetailInstanceResponse instanceResponse = copyServerProperties(server);
        //???????????????volume??????
        List<DetailVolumeResponse> volumeDetailList = copyVolumeProperties(server);

        instanceModel.setDetail(instanceResponse);
        instanceModel.setVolumeDetailList(volumeDetailList);
        instanceModel.setVolDiffBitmap(new HashMap<>());

        List<DetailPortResponse> detailPorts = copyPortProperties(server);
        instanceModel.setPorts(detailPorts);

    }

    /**
     * ?????????????????????????????????????????????
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
     * ??????volume?????????????????????????????????
     * poolname?????????????????????????????????????????????????????????
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
     * ??????port??????????????????
     * ?????????????????????
     */
    private List<DetailPortResponse> copyPortProperties(NovaServer server) throws QueryPortException {
        //???????????????????????????
        List<NeutronPort> portLists = this.access.getPortList();
        //?????????ip??????
        List<String> ips = new ArrayList<>();
        server.getAddresses().getAddresses().forEach((key, value) -> {
            String ip = value.stream().map(Address::getAddr).collect(Collectors.toList()).get(0);
            ips.add(ip);
        });
        List<DetailPortResponse> list = new ArrayList<>();
        //?????? ???????????????????????????server??????????????? ???need optimization
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
     * ??????id?????????rbd??????spec
     */
    private Map<String, String> resId2ImageSpec = new HashMap<>();

    /**
     * ??????id??????
     */
    private List<String> resIds = new ArrayList<>();

    /**
     * ?????????rbd??????spec??????
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
     * ??????id???????????????snapshot?????????
     */
    private Map<String, String> resId2SnapId = new HashMap<>();

    /**
     * ????????????
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
     * ?????????????????????????????????????????????
     */
    private Map<String, Boolean> isFullBackup = new HashMap<>();

    /**
     * ???????????????????????????
     */
    @Procedure
    private void initExportTypes(boolean isFullByDefault) {
        for (String resId : this.resIds) {
            boolean hasOverlap = this.generationInfo.getResId2snap().containsKey(resId);
            isFullBackup.put(resId, isFullByDefault || !hasOverlap);
        }
    }

    /**
     * ?????????????????????????????????
     */
    private Map<String, Integer> resTimeoutLimit = new HashMap<>();

    /**
     * ????????????????????????????????????
     */
    @Procedure
    private void initResTimeoutLimit() {
        //?????????????????????????????????????????????
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
     * ??????????????????????????????????????????????????????
     */
    @Procedure
    private void buildBackupContext(String instanceId)
            throws LocalSnapshotException, LocalTruncateException {
        for (String resId : this.resIds) {
            boolean isFull = this.isFullBackup.get(resId);
            //????????????????????????????????????????????????
            if (isFull) {
                long byteSize =  this.instanceModel.getVolumeDetailList().stream()
                        .filter(volume -> (resId).equals(volume.getId()))
                        .mapToInt(DetailVolumeResponse::getSize)
                        .sum() * 1024 * 1024 * 1024L;
                String tmpDir = this.getTemporaryContentDir(instanceId);
                String dest = Paths.get(tmpDir, resId).toString();
                LocalAccess.os().truncateFile(dest, byteSize);
            }
            //??????????????????????????????????????????????????????????????????mdfs??????
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
     * ??????????????????
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
     * ??????????????????
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
        //??????????????????
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
     * ??????????????????????????????
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
     * ???????????????
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
     * ??????????????????
     */
    @Procedure
    private void recordBackupModel(String instanceId, String generationNum)
            throws LocalWriteException {
        String recordFile = this.getInstanceModelFile(instanceId, generationNum);
        Gson gson = new Gson();
        LocalAccess.os().write(gson.toJson(instanceModel), recordFile);
    }

    /**
     * ??????????????????
     */
    @Procedure
    private void removeExpiredSnaps()
            throws DeleteSnapshotException {
        for (Map.Entry<String, String> entry : this.generationInfo.getResId2snap().entrySet()) {
            this.access.deleteSnapshot(entry.getValue());
        }
    }

    /**
     * ??????????????????
     */
    @Procedure
    private void finalizeBackupService(String instanceId, String generationNum)
            throws LocalWriteException {
        //????????????????????????
        this.generationInfo.setLastSuccessGen(generationNum);
        this.generationInfo.setResId2snap(this.resId2SnapId);
        String infoFile = this.getGenerationFile(instanceId);
        Gson gson = new Gson();
        LocalAccess.os().write(gson.toJson(this.generationInfo), infoFile);
        //??????nfs
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
            //????????????????????????
            try {
                this.access.deleteSnapshot(entry.getValue());
                logger.info("DELETED SNAPSHOT: {}", entry.getValue());
            }catch (BaseException e) {
                logger.error("{}: {}", e.getCode().getCode(), e.getCode().getMessage());
            }
            //???????????????????????????????????????
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
       // ??????nfs
        this.dischargeAgentPool();
    }
}
