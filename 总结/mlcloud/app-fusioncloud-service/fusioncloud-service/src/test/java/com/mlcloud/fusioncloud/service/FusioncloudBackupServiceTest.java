package com.mlcloud.fusioncloud.service;

import com.google.gson.Gson;
import com.mlcloud.defination.bean.service.GenerationInfoBean;
import com.mlcloud.fusioncloud.FusinonCloudAccess;
import com.mlcloud.fusioncloud.bean.*;
import com.mlcloud.fusioncloud.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.network.IP;
import org.openstack4j.openstack.compute.domain.NovaSecurityGroup;
import org.openstack4j.openstack.compute.domain.NovaServer;
import org.openstack4j.openstack.networking.domain.NeutronIP;
import org.openstack4j.openstack.networking.domain.NeutronPort;
import org.openstack4j.openstack.storage.block.domain.CinderVolume;
import org.openstack4j.openstack.storage.block.domain.CinderVolumeAttachment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：hf
 * @date ：Created in 2020/12/22 5:13 下午
 */
@Slf4j
@Ignore
public class FusioncloudBackupServiceTest {

    private FusinonCloudAccess access;
    private Gson gson;

    @Before
    public void BeforeClass(){
        String osProjectDomainName = "default";
        String osUserDomainName = "default";
        String osUserName = "admin";
        String osPassword = "123456";
        String serverIp = "192.168.15.248";
        String port = "5000";
        boolean is_openstack = true;
        String osProjectId = "0869f5b3b3d3419fb49275e57002eb92";

        OpenRcBean openRcBean = new OpenRcBean(osProjectId, osProjectDomainName, osUserDomainName, osUserName,
                osPassword, serverIp, port, is_openstack);
        FusinonCloudAccess access = new FusinonCloudAccess(openRcBean);
        this.gson = new Gson();
        this.access = access;
    }

    @Test
    public void checkPrerequisiteTest(){
        //
    }

    @Test
    public void initBackupService(){
        //
    }

    //instanced的详情模型
    private InstanceModel instanceModel = new InstanceModel();

    /**
     * 实例详情文件：
     * /media/pool0/vm/192.168.15.248/ars/c9dd7e5f-d493-40ad-b238-5978e0a768e3/2/c9dd7e5f-d493-40ad-b238-5978e0a768e3.json
     */
    @Test
    public void queryInstanceDetailTest() throws QueryInstanceException, QueryVolumeException, QueryPortException {
        String instanceId = "6d680099-425a-4092-8b42-480620027fa4";
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

        log.info(gson.toJson(instanceModel));
    }
    /**
     * 查询实例详情，只截取有用的字段，降低io
     * 没有采用浅拷贝，坑有点多
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
                .build();
    }

    /**
     * 查询volume详情，只截取有用的字段，降低io
     * 没有采用浅拷贝
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

    @Test
    public void initImageSpecsTest() throws QueryInstanceException,
            QueryVolumeException, QueryPortException {
        queryInstanceDetailTest();

        List<DetailVolumeResponse> volumeDetailList = this.instanceModel.getVolumeDetailList();
        volumeDetailList.forEach(volumeDetail -> {
            String volumeId = volumeDetail.getId();
            String poolName = volumeDetail.getPoolName();
            String imageSpec = this.getImageSpec(poolName, volumeDetail.getId());
            this.resId2ImageSpec.put(volumeId, imageSpec);
            this.resIds.add(volumeId);
        });

        log.info(gson.toJson(this.resId2ImageSpec));
        log.info(gson.toJson( this.resIds));
    }

    String getImageSpec(String poolName, String resId){
        return String.format("%s/volume-%s", poolName, resId);
    }

    private Map<String, String> resId2SnapId = new HashMap<>();

    @Test
    public void createSnapshotsTest() throws QuerySnapshotException,
            CreateSnapshotTimeoutException, CreateSnapshotException {

        for (String resId : this.resIds) {
            String snapId = createSnapshot(resId);
            resId2SnapId.put(resId, snapId);
        }

        log.info(gson.toJson(resId2SnapId));
    }

    private String createSnapshot(String resId)
            throws CreateSnapshotException, CreateSnapshotTimeoutException, QuerySnapshotException {
        String snapshotName = getSnapshotName();
        return this.access.createSnapshot(resId, snapshotName);
    }

    String getSnapshotName(){
        return String.format("mulangcloud_%s", UUID.randomUUID());
    }

    private Map<String, Boolean> isFullBackup = new HashMap<>();

    private GenerationInfoBean generationInfo;

    @Test
    public void initExportTypesTest(){
        boolean isFullByDefault = false;
        String content = "";
        generationInfo = gson.fromJson(content, GenerationInfoBean.class);

        for (String resId : this.resIds) {
            boolean hasOverlap = this.generationInfo.getResId2snap().containsKey(resId);
            isFullBackup.put(resId, isFullByDefault || !hasOverlap);
        }

        log.info(gson.toJson(isFullBackup));
    }

    /**
     * 记录备份资源的超时限制
     */
    private Map<String, Integer> resTimeoutLimit = new HashMap<>();

    @Test
    public void initResTimeoutLimitTest() throws QueryInstanceException,
            QueryVolumeException, QueryPortException {

        queryInstanceDetailTest();
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
        for (Map.Entry<String, Integer> entry : resTimeoutLimit.entrySet()) {
            String volumeId = entry.getKey();
            Integer timeLimit = entry.getValue();

            log.info("volume:{} 的超时限制时间为:{} ",volumeId,timeLimit);
        }
    }

    @Test
    public void buildBackupContextTest(){
        //
    }

    @Test
    public void exportImagesTest(){
        //
    }

    @Test
    public void exportDiffBitmapTest(){
        //
    }

    @Test
    public void persistentBackupImagesTest(){
        //
    }

    @Test
    public void recordBackupModelTest(){
        //
    }

    @Test
    public void removeExpiredSnapsTest() throws DeleteSnapshotException {
        String content = "";
        generationInfo = gson.fromJson(content, GenerationInfoBean.class);

        for (Map.Entry<String, String> entry : this.generationInfo.getResId2snap().entrySet()) {
            this.access.deleteSnapshot(entry.getValue());
        }
    }


}
