package com.mlcloud.fusioncloud.access;

import com.google.gson.Gson;
import com.mlcloud.fusioncloud.FusinonCloudAccess;
import com.mlcloud.fusioncloud.bean.InstanceModel;
import com.mlcloud.fusioncloud.bean.OpenRcBean;
import com.mlcloud.fusioncloud.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.BlockDeviceMappingCreate;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.openstack.compute.domain.NovaServer;
import org.openstack4j.openstack.compute.domain.ext.ExtHypervisor;
import org.openstack4j.openstack.networking.domain.NeutronIP;
import org.openstack4j.openstack.networking.domain.NeutronPort;
import org.openstack4j.openstack.networking.domain.NeutronPortCreate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author ：hf
 * @date ：Created in 2020/12/13 4:41 下午
 * @description：
 * @modified By：
 * @version: $
 */
@Slf4j
//@Ignore
public class FusionCloudAccessTest {

    private  String osProjectId = "0869f5b3b3d3419fb49275e57002eb92";
    private  String osProjectDomainName = "default";
    private  String osUserDomainName = "default";
    private  String osUserName = "admin";
    private  String osPassword = "123456";
    private  String serverIp = "192.168.15.248";
    private  String port = "5000";
    private  Boolean openstack = true;

    private  FusinonCloudAccess access;
    private  Gson gson;
    private  OSClient<OSClient.OSClientV3> osClient;

    private static final String ACTIVE = "ACTIVE";
    private static final String IN_USE = "IN_USE";
    private static final String AVAILABLE = "AVAILABLE";
    /**
     * 原生的openstack的pool池 为vms ，一般默认为volumes
     */
    private static final String POOLNAME = "volumes";

    @Before
    public void BeforeClass(){
        OpenRcBean openRcBean = new OpenRcBean(osProjectId,osProjectDomainName,osUserDomainName,osUserName,osPassword,serverIp,port,openstack);
        this.osClient = openRcBean.getOpenstackClient();
        this.access = new FusinonCloudAccess(openRcBean);
        this.gson = new Gson();
    }

    /**
     * ===================COMPUTE ===========================================
     */

    @Test
    public void getInstanceListTest() throws QueryInstanceException {
        List<NovaServer> servers = this.access.getInstanceList();
        for (Server server : servers) {
            log.info(gson.toJson(server));
        }
    }

    @Test
    public void getInstanceDetailTest() throws QueryInstanceException {
        String serverId = "c9dd7e5f-d493-40ad-b238-5978e0a768e3";
        NovaServer server = access.getInstanceDetail(serverId);
        log.info(gson.toJson(server));
    }
    @Test
    public void createInstanceTest() throws CreateInstanceTimeoutException {
        String instanceName = "test4clone-002";
        String flavorId = "d284d1cb-fee0-49d3-9f89-6d2375491c7c";
        String adminPassword = "123456";
        List<String> networkIds = Arrays.asList("55877aec-3962-4d0f-b21d-29f25dfadaff");
        String availableZone = "nova";
        String sysVolumeId = "";
//      List<String> securityGroups = new ArrayList<>();
        List<String> securityGroups = Arrays.asList("default");
        //获取开始运行时间
        long startTime = System.currentTimeMillis();
        //创建启动卷块设备模型
        BlockDeviceMappingCreate bdc = Builders.blockDeviceMapping()
                .uuid(sysVolumeId)
                .bootIndex(0)
                .build();
        //使用块设备模型创建虚拟机
        ServerCreateBuilder scBuilder = Builders.server()
                .name(instanceName)
                .flavor(flavorId)
                .blockDevice(bdc)
                .networks(networkIds)
                .addAdminPass(adminPassword)
                .availabilityZone(availableZone);
        //设置安全组
        if( securityGroups != null && !securityGroups.isEmpty() ){
            securityGroups.forEach(scBuilder::addSecurityGroup);
        }

        Server server = this.osClient.compute().servers().boot(scBuilder.build());
        String instanceId = server.getId();

        wait4InstanceCreating(instanceId,sysVolumeId);
        long endTime = System.currentTimeMillis();
        log.info("创建程序运行时间：" + (endTime - startTime) + "ms");
    }

    private void wait4InstanceCreating(String instanceId, String sysVolumeId) throws
            CreateInstanceTimeoutException {
//        Server detailInstance = detailInstance(instanceId);
//        Volume sysVolume = detailVolume(sysVolumeId);
        Server detailInstance = this.osClient.compute().servers().get(instanceId);
        Volume sysVolume = this.osClient.blockStorage().volumes().get(sysVolumeId);

        int size = sysVolume.getSize();
        int retry = getRetryNum(size);
        String statusName;
        statusName = Optional.ofNullable(detailInstance.getStatus())
                .map(Enum::name)
                .orElse("");
        while (!ACTIVE.equals(statusName) && retry-- > 0) {
            pause();
            detailInstance = this.osClient.compute().servers().get(instanceId);
        }
        statusName = Optional.ofNullable(detailInstance.getStatus())
                .map(Enum::name)
                .orElse("");
        if (!ACTIVE.equals(statusName)) {
            throw new CreateInstanceTimeoutException(instanceId);
        }
    }

    @Test
    public void deleteInstanceTest() throws  DeleteInstanceException {
        //获取开始运行时间
        long startTime = System.currentTimeMillis();
        String instanceId = "1c4023de-c0a3-46ef-9b9d-acdbf65a404b";
        this.access.deleteInstance(instanceId);
        long endTime = System.currentTimeMillis();
        log.info("删除实例程序运行时间：" + (endTime - startTime) + "ms");
    }

    @Test
    public void powerOffInstanceTest() throws PowerOffTimeoutException, PowerOffInstanceException, QueryInstanceException {
        String instanceId = "6d680099-425a-4092-8b42-480620027fa4";
        long startTime = System.currentTimeMillis();
        try {
            this.osClient.compute().servers().action(instanceId, Action.STOP);
        } catch (Exception e) {
            throw new PowerOffInstanceException(e.getMessage());
        }
        wait4InstancePowerOff(instanceId);
        long endTime = System.currentTimeMillis();
        log.info("关闭实例程序运行时间：" + (endTime - startTime) + "ms");
    }

    private void wait4InstancePowerOff(String instanceId) throws  PowerOffTimeoutException {
        Server detailInstance = this.osClient.compute().servers().get(instanceId);
        int retry = 60;
        String statusName = Optional.ofNullable(detailInstance.getStatus())
                .map(Enum::name)
                .orElse("");
        while (!("SHUTOFF").equals(statusName) && (retry-- > 0)) {
            pause();
            detailInstance = this.osClient.compute().servers().get(instanceId);
            statusName = Optional.ofNullable(detailInstance.getStatus())
                    .map(Enum::name)
                    .orElse("");
        }
        log.info("power off  retry======={}",retry);
        if (!("SHUTOFF").equals(statusName)) {
            throw new PowerOffTimeoutException(instanceId);
        }
    }

    @Test
    public void powerOnInstanceTest() throws PowerOnInstanceException, PowerOnTimeoutException {
        String instanceId = "6d680099-425a-4092-8b42-480620027fa4";
        long startTime = System.currentTimeMillis();
        try {
            this.osClient.compute().servers().action(instanceId, Action.START);
        } catch (Exception e) {
            throw new PowerOnInstanceException(e.getMessage());
        }
        wait4InstancePowerOn(instanceId);
        long endTime = System.currentTimeMillis();
        log.info("开机实例程序运行时间：" + (endTime - startTime) + "ms");
    }

    private void wait4InstancePowerOn(String instanceId)
            throws  PowerOnTimeoutException {
        Server detailInstance = this.osClient.compute().servers().get(instanceId);
        int retry = 30;
        String statusName = Optional.ofNullable(detailInstance.getStatus())
                .map(Enum::name)
                .orElse("UNKNOWN");

        while (!ACTIVE.equals(statusName) && (retry-- > 0)) {
            pause();
            detailInstance = this.osClient.compute().servers().get(instanceId);
            statusName = Optional.ofNullable(detailInstance.getStatus())
                    .map(Enum::name)
                    .orElse("UNKNOWN");
        }
        log.info("power on retry======={}",retry);
        if (!ACTIVE.equals(statusName)) {
            throw new PowerOnTimeoutException(instanceId);
        }
    }

    @Test
    public void attachVolume() throws AttachVolumeException, QueryVolumeException, AttachVolumeTimeoutException {
        String serverId = "8a6ac17a-9743-4d0b-97fc-062643e1b1fe";
        String volumeId = "6d94e240-09c7-45e6-9acf-8f7a8eaf4062";
        String device = "/dev/vdd";
        long startTime = System.currentTimeMillis();
        access.attachVolume(serverId,volumeId,device);
        long endTime = System.currentTimeMillis();
        log.info("挂载1g的卷，程序运行时间：" + (endTime - startTime) + "ms");
    }

    @Test
    public void detachVolumeTest() throws
            DetachVolumeException, DetachVolumeTimeoutException {
        String serverId = "bb07f8c3-8110-451a-8f29-32698fe8229f";
        String volumeId = "00049ac7-64f1-4458-b4f6-8a2753e3cfcf";

        try {
            ActionResponse actionResponse = this.osClient.compute().servers().detachVolume(serverId, volumeId);
            if (200 != actionResponse.getCode()) {
                throw new DetachVolumeException(actionResponse.getFault());
            }
        } catch (Exception e) {
            throw new DetachVolumeException(e.getMessage());
        }
        wait4VolumeDetaching(volumeId);
    }

    private void wait4VolumeDetaching(String volumeId)
            throws DetachVolumeTimeoutException {
        Volume detailVolume = this.osClient.blockStorage().volumes().get(volumeId);

        int retry = this.getRetryNum(detailVolume.getSize());
        log.info("volume detach retry = {}", retry);
        String statusName = detailVolume.getStatus().name();
        while (!AVAILABLE.equals(statusName) && (retry-- > 0)) {
            pause();
            detailVolume = this.osClient.blockStorage().volumes().get(volumeId);
            statusName = detailVolume.getStatus().name();
        }
        if (!AVAILABLE.equals(statusName)) {
            throw new DetachVolumeTimeoutException(volumeId);
        }
        log.info(" volume success detach retry = {}", retry);
    }

    @Test
    public void getPhysicalHostListTest() throws QueryPhysicalHostException {
        List<ExtHypervisor> physicalHostList = this.access.getPhysicalHostList();
        physicalHostList.forEach(extHypervisor -> {
            log.info(gson.toJson(extHypervisor));
        });
    }

    /**
     * ===================CINDER BLOCK STORAGE   ============================
     */


    @Test
    public void DetailVolumes() throws QueryVolumeException {
        String volumeId = "6d94e240-09c7-45e6-9acf-8f7a8eaf4062";
        Volume volume = access.getVolumeDetail(volumeId);
        Gson gson = new Gson();
        log.info(gson.toJson(volume));
    }

    @Test
    public void deleteVolume() throws DeleteVolumeException {
        String volumeId = "6aa32775-c2aa-40a7-952e-e1b0fc43f1ed";
        this.access.deleteVolume(volumeId);
    }

    @Test
    public void createEmptyVolumeTest() throws CreateVolumeException,
            QueryVolumeException, CreateVolumeTimeoutException {
        int size = 1;
        String volumeType = "ceph";
        String volumeName = "test001";
        boolean bootbale = false;

        this.access.createEmptyVolume(size,volumeType,volumeName,bootbale);
    }

    @Test
    public void createSnapshotTest() throws QuerySnapshotException,
            CreateSnapshotTimeoutException, CreateSnapshotException {

        String volumeId = "";
        String snapshotName = "";
        this.access.createSnapshot(volumeId,snapshotName);
    }

    @Test
    public void getVolumeSnapshotDetailTest() throws QuerySnapshotException {
        String snapshotId = "";
        this.access.getVolumeSnapshotDetail(snapshotId);
    }

    @Test
    public void deleteSnapshotTest(){

    }
    /**
     * ===================NETWORK   ============================
     */
    @Test
    public void getNICDetailTest() throws QueryNICException {
        String portId = "73ae0189-b915-4943-8690-3a377e50e54b";
        NeutronPort nicDetail = this.access.getNICDetail(portId);
        log.info(gson.toJson(nicDetail));
    }

    @Test
    public void createNICTest() throws QueryNICException, DetachNICException {

        String portId = "221968db-4c89-4e1e-ac13-9764671c13a8";
        NeutronPort port = this.access.getNICDetail(portId);

        String networkId = port.getNetworkId();
        String nicName = port.getName();
        Set< NeutronIP > fixedIps = (Set<NeutronIP>)port.getFixedIps();
        String macAddress = port.getMacAddress();

        this.access.detachNIC("4215e74b-dcd4-4269-87df-99293c857b08","221968db-4c89-4e1e-ac13-9764671c13a8");

        NeutronPortCreate portCreate = new NeutronPortCreate();

    }
    @Test
    public void deleteNicTest() throws DeleteNICException, QueryNICException, AttachNICException {
        String portId = "f6a0ad8a-1f5a-43c7-bced-b27fd85d86d1";
        this.access.deleteNIC(portId);
    }

    @Test
    public void attachNICTest() throws AttachNICException {
        String networkId = "ffb80abc-f8b9-461d-9bc6-9c17fc8211ce";
        NeutronPort testPort1 = (NeutronPort) this.osClient.networking().port().create(NeutronPort.builder().name("testPort1")
                .networkId(networkId)
                .build()
        );
        log.info(gson.toJson(testPort1));
        this.access.attachNIC("4215e74b-dcd4-4269-87df-99293c857b08",testPort1.getId());
    }
    /**
     *=======================common ===========================
     */

    @Test
    public void testParseObject(){
        String modelStr = "{\"server\":{\"accessIPv4\":\"\",\"accessIPv6\":\"\",\"addresses\":{\"addresses\":{\"provider\":[{\"addr\":\"192.168.12.15\",\"macAddr\":\"fa:16:3e:53:be:d2\",\"type\":\"fixed\",\"version\":4}]}},\"availabilityZone\":\"nova\",\"configDrive\":\"\",\"created\":1604852888000,\"diskConfig\":\"AUTO\",\"flavor\":{\"disabled\":false,\"disk\":20,\"ephemeral\":0,\"id\":\"d284d1cb-fee0-49d3-9f89-6d2375491c7c\",\"links\":[{\"href\":\"http://controller:8774/v2.1/flavors/d284d1cb-fee0-49d3-9f89-6d2375491c7c\",\"rel\":\"self\"},{\"href\":\"http://controller:8774/flavors/d284d1cb-fee0-49d3-9f89-6d2375491c7c\",\"rel\":\"bookmark\"}],\"name\":\"centos-test-hf\",\"public\":true,\"ram\":2048,\"rxtxCap\":0,\"rxtxFactor\":1.0,\"rxtxQuota\":0,\"swap\":0,\"vcpus\":2},\"flavorId\":\"d284d1cb-fee0-49d3-9f89-6d2375491c7c\",\"host\":\"localhost.localdomain\",\"hostId\":\"edd4ff9dd1fc7ab1d28aa89e6302a2f4ec255286a6cc689712eae93f\",\"hypervisorHostname\":\"localhost.localdomain\",\"id\":\"233a8eda-c9d8-496e-b97c-39c74050b185\",\"image\":{\"created\":1604852050000,\"id\":\"b760a3fe-a75d-4874-8b8a-cdbf76fbf29c\",\"links\":[{\"href\":\"http://controller:8774/v2.1/images/b760a3fe-a75d-4874-8b8a-cdbf76fbf29c\",\"rel\":\"self\"},{\"href\":\"http://controller:8774/images/b760a3fe-a75d-4874-8b8a-cdbf76fbf29c\",\"rel\":\"bookmark\"},{\"href\":\"http://controller:9292/images/b760a3fe-a75d-4874-8b8a-cdbf76fbf29c\",\"rel\":\"alternate\",\"type\":\"application/vnd.openstack.image\"}],\"metaData\":{\"description\":\"hf测试用的镜像\"},\"minDisk\":0,\"minRam\":0,\"name\":\"centos7-test-hf\",\"progress\":100,\"size\":830472192,\"snapshot\":false,\"status\":\"ACTIVE\",\"updated\":1604852078000},\"imageId\":\"b760a3fe-a75d-4874-8b8a-cdbf76fbf29c\",\"instanceName\":\"instance-000000db\",\"launchedAt\":1604852938000,\"links\":[{\"href\":\"http://controller:8774/v2.1/servers/233a8eda-c9d8-496e-b97c-39c74050b185\",\"rel\":\"self\"},{\"href\":\"http://controller:8774/servers/233a8eda-c9d8-496e-b97c-39c74050b185\",\"rel\":\"bookmark\"}],\"metadata\":{},\"name\":\"hf-test\",\"osExtendedVolumesAttached\":[\"c94e3bec-3e62-41af-bb7d-caba7fd42918\"],\"powerState\":\"1\",\"progress\":0,\"securityGroups\":[{\"name\":\"default\"}],\"status\":\"ACTIVE\",\"tenantId\":\"0869f5b3b3d3419fb49275e57002eb92\",\"updated\":1606898678000,\"userId\":\"95df952831614b6f844ee6cb71f7cef9\",\"vmState\":\"active\"},\"ports\":[{\"adminStateUp\":true,\"allowedAddressPairs\":[],\"deviceId\":\"233a8eda-c9d8-496e-b97c-39c74050b185\",\"deviceOwner\":\"compute:nova\",\"fixedIps\":[{\"ipAddress\":\"192.168.12.15\",\"subnetId\":\"4922a1d2-b235-4d15-82b1-d5f17569c49d\"}],\"hostId\":\"localhost.localdomain\",\"id\":\"dd4ab678-2636-4972-8a4e-6cf190d9f96f\",\"macAddress\":\"fa:16:3e:53:be:d2\",\"name\":\"\",\"networkId\":\"f16bf9df-8fff-4c15-9cf2-387b47799823\",\"portSecurityEnabled\":true,\"profile\":{},\"securityGroups\":[\"6b16ab84-0f52-4b8a-8bae-53a322b08690\"],\"state\":\"ACTIVE\",\"tenantId\":\"0869f5b3b3d3419fb49275e57002eb92\",\"vNicType\":\"normal\",\"vifDetails\":{\"port_filter\":true},\"vifType\":\"bridge\"}],\"volDiffBitmap\":{\"c94e3bec-3e62-41af-bb7d-caba7fd42918\":[]},\"volumeDetailList\":[{\"bootable\":false,\"device\":\"/dev/vda\",\"id\":\"c94e3bec-3e62-41af-bb7d-caba7fd42918\",\"poolName\":\"volumes\",\"size\":2,\"status\":\"ceph\",\"type\":\"ceph\",\"volumeName\":\"hf_test\"}]}";
        InstanceModel model2Restore = null;
        Gson gson = new Gson();
        model2Restore = gson.fromJson(modelStr, InstanceModel.class);
        log.info(gson.toJson(model2Restore));
    }


    private int getRetryNum(int coe) {
        int cal = coe * 20 + 5;
        if (cal < 0) {
            cal = 3600 * 24;
        }
        return cal > 3600 * 24 ? 3600 : cal;
    }

    private void pause() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
            Thread.currentThread().interrupt();
        }
    }
}
