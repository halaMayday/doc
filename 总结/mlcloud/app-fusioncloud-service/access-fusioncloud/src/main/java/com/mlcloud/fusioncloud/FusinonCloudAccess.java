package com.mlcloud.fusioncloud;

import com.mlcloud.fusioncloud.bean.OpenRcBean;
import com.mlcloud.fusioncloud.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.BlockDeviceMappingCreate;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.compute.domain.NovaServer;
import org.openstack4j.openstack.compute.domain.ext.ExtHypervisor;
import org.openstack4j.openstack.networking.domain.NeutronNetwork;
import org.openstack4j.openstack.networking.domain.NeutronPort;
import org.openstack4j.openstack.storage.block.domain.CinderVolume;
import org.openstack4j.openstack.storage.block.domain.CinderVolumeSnapshot;

import java.util.List;
import java.util.Optional;

/**
 * @author ：hf
 * @date ：Created in 2020/11/30 5:22 下午
 * @description：
 * @modified By：
 * @version: $
 */
@Slf4j
public class FusinonCloudAccess {

    private OSClient.OSClientV3 osClient;

    private static final String ACTIVE = "ACTIVE";
    private static final String IN_USE = "IN_USE";
    private static final String AVAILABLE = "AVAILABLE";

    public FusinonCloudAccess(OpenRcBean openRcBean) {
        this.osClient = openRcBean.getOpenstackClient();
    }

    public static FusinonCloudAccess getAccess(OpenRcBean openRcBean) {
        return new FusinonCloudAccess(openRcBean);
    }

    /**
     * @description:
     * @param:
     * @return: java.util.List<? extends org.openstack4j.model.compute.Server>
     */
    @SuppressWarnings("unchecked")
    public List<NovaServer> getInstanceList() throws QueryInstanceException {
        List<NovaServer> result;
        try {
            result = (List<NovaServer>)this.osClient.compute().servers().list();
        } catch (Exception e) {
            throw new QueryInstanceException(e.getMessage());
        }
        return result;
    }

    public NovaServer getInstanceDetail(String instanceId) throws QueryInstanceException {
        try {
            return (NovaServer)this.osClient.compute().servers().get(instanceId);
        } catch (Exception e) {
            throw new QueryInstanceException(e.getMessage());
        }
    }

    /**
     * @description： 创建空白虚拟机
     * @param instanceName：
     * @param flavorId:
     * @param adminPassword:
     * @param networkIds:
     * @param availableZone:
     * @param sysVolumeId:
     * @return : java.lang.String
     */
    public String createInstance(String instanceName, String flavorId, String adminPassword,
                                 List<String> networkIds, String availableZone, String sysVolumeId, List<String> securityGroups)
            throws CreateInstanceException, CreateInstanceTimeoutException, QueryInstanceException, QueryVolumeException {
        String instanceId = "";
        try {
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
                                                    .addAdminPass(adminPassword)
                                                    .availabilityZone(availableZone)
                                                    .networks(networkIds);
            //设置安全组
            if( (securityGroups !=null) && (!securityGroups.isEmpty()) ){
                securityGroups.forEach(scBuilder::addSecurityGroup);
            }
            NovaServer server = (NovaServer)this.osClient.compute().servers().boot(scBuilder.build());
            instanceId = server.getId();
        } catch (Exception e) {
            throw new CreateInstanceException(e.getMessage());
        }
        wait4InstanceCreating(instanceId,sysVolumeId);
        return instanceId;
    }

    /**
     *
     * @param instanceId ：
     * @throws QueryInstanceException
     * @throws CreateInstanceTimeoutException
     */
    private void wait4InstanceCreating(String instanceId, String sysVolumeId) throws
            QueryInstanceException, CreateInstanceTimeoutException, QueryVolumeException {
        NovaServer detailInstance = getInstanceDetail(instanceId);
        CinderVolume sysVolume = getVolumeDetail(sysVolumeId);
        int size = sysVolume.getSize();
        int retry = getRetryNum(size);
        String statusName;
        statusName = Optional.ofNullable(detailInstance.getStatus())
                .map(Enum::name)
                .orElse("");
        while (!ACTIVE.equals(statusName) && retry-- > 0) {
            pause();
            detailInstance = getInstanceDetail(instanceId);
        }
        statusName = Optional.ofNullable(detailInstance.getStatus())
                .map(Enum::name)
                .orElse("");
        if (!ACTIVE.equals(statusName)) {
            throw new CreateInstanceTimeoutException(instanceId);
        }
    }

    /**
     * @param instanceId
     * @return void
     * @description: 删除server实例
     */
    public void deleteInstance(String instanceId) throws DeleteInstanceException {
        ActionResponse actionResponse ;
        try {
            actionResponse = this.osClient.compute().servers().delete(instanceId);
        } catch (Exception e) {
            throw new DeleteInstanceException(e.getMessage());
        }
        if(!actionResponse.isSuccess()){
            throw new DeleteInstanceException(actionResponse.getFault());
        }
    }

    public void powerOffInstance(String instanceId) throws PowerOffInstanceException,
            QueryInstanceException, PowerOffTimeoutException {
        ActionResponse actionResponse ;
        try {
            actionResponse = this.osClient.compute().servers().action(instanceId, Action.STOP);
        } catch (Exception e) {
            throw new PowerOffInstanceException(e.getMessage());
        }
        if(!actionResponse.isSuccess()){
            throw new PowerOffInstanceException(actionResponse.getFault());
        }
        wait4InstancePowerOff(instanceId);
    }

    /**
     * 关机耗时 retry 次数多次本地测试为 30次左右
     */
    private void wait4InstancePowerOff(String instanceId) throws QueryInstanceException, PowerOffTimeoutException {
        NovaServer detailInstance = getInstanceDetail(instanceId);
        int retry = 60;
        String statusName = Optional.ofNullable(detailInstance.getStatus())
                .map(Enum::name)
                .orElse("");
        while (!("SHUTOFF").equals(statusName) && (retry-- > 0)) {
            pause();
            detailInstance = getInstanceDetail(instanceId);
            statusName = Optional.ofNullable(detailInstance.getStatus())
                    .map(Enum::name)
                    .orElse("");
        }
        if (!("SHUTOFF").equals(statusName)) {
            throw new PowerOffTimeoutException(instanceId);
        }
    }

    public void powerOnInstance(String instanceId) throws
            PowerOnInstanceException, QueryInstanceException, PowerOnTimeoutException {
        ActionResponse actionResponse;
        try {
             actionResponse = this.osClient.compute().servers().action(instanceId, Action.START);
        } catch (Exception e) {
            throw new PowerOnInstanceException(e.getMessage());
        }
        if(!actionResponse.isSuccess()){
            throw new PowerOnInstanceException(actionResponse.getFault());
        }
        wait4InstancePowerOn(instanceId);
    }

    /**
     * 开机耗时 retry 次数多次本地测试为 40次左右
     */
    private void wait4InstancePowerOn(String instanceId)
            throws QueryInstanceException, PowerOnTimeoutException {
        NovaServer detailInstance = getInstanceDetail(instanceId);
        int retry = 30;
        String statusName = Optional.ofNullable(detailInstance.getStatus())
                .map(Enum::name)
                .orElse(" ");

        while (!ACTIVE.equals(statusName) && (retry-- > 0)) {
            pause();
            detailInstance = getInstanceDetail(instanceId);
            statusName = Optional.ofNullable(detailInstance.getStatus())
                    .map(Enum::name)
                    .orElse(" ");
        }

        if (!ACTIVE.equals(statusName)) {
            throw new PowerOnTimeoutException(instanceId);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ExtHypervisor> getPhysicalHostList() throws QueryPhysicalHostException {
        try {
            return (List<ExtHypervisor>) this.osClient.compute().hypervisors().list();
        } catch (Exception e) {
            throw new QueryPhysicalHostException(e.getMessage());
        }
    }

    /**
     * @param serverId
     * @param device
     * @description: Attaches a volume to a server.
     * You should set instance_uuid or host_name. Volume status must be available.
     * @param: volumeId
     * @return: void
     */
    public void attachVolume(String serverId, String volumeId, String device)
            throws AttachVolumeException, QueryVolumeException, AttachVolumeTimeoutException {
        try {
            this.osClient.compute().servers().attachVolume(serverId, volumeId, device);
        } catch (Exception e) {
            throw new AttachVolumeException(e.getMessage());
        }
        wait4VolumeAttaching(volumeId);
    }

    private void wait4VolumeAttaching(String volumeId)
            throws QueryVolumeException, AttachVolumeTimeoutException {

        CinderVolume detailVolume = this.getVolumeDetail(volumeId);
        long retry = this.getRetryNum(detailVolume.getSize());
        String statusName = detailVolume.getStatus().name();
        while (!IN_USE.equals(statusName) && (retry-- > 0)) {
            pause();
            detailVolume = this.getVolumeDetail(volumeId);
            statusName = detailVolume.getStatus().name();
        }
        if (!IN_USE.equals(statusName)) {
            throw new AttachVolumeTimeoutException(volumeId);
        }
    }

    public void detachVolume(String serverId, String volumeId) throws
            DetachVolumeException, QueryVolumeException, DetachVolumeTimeoutException {
        ActionResponse actionResponse;
        try {
             actionResponse = this.osClient.compute().servers().detachVolume(serverId, volumeId);
        } catch (Exception e) {
            throw new DetachVolumeException(e.getMessage());
        }

        if(!actionResponse.isSuccess()){
            throw new DetachVolumeException(actionResponse.getFault());
        }

        wait4VolumeDetaching(volumeId);
    }

    private void wait4VolumeDetaching(String volumeId)
            throws QueryVolumeException, DetachVolumeTimeoutException {
        CinderVolume detailVolume = this.getVolumeDetail(volumeId);
        int retry = this.getRetryNum(detailVolume.getSize());
        String statusName = detailVolume.getStatus().name();
        while (!AVAILABLE.equals(statusName) && (retry-- > 0)) {
            pause();
            detailVolume = this.getVolumeDetail(volumeId);
            statusName = detailVolume.getStatus().name();
        }
        if (!AVAILABLE.equals(statusName)) {
            throw new DetachVolumeTimeoutException(volumeId);
        }
    }

    //network

    public NeutronPort getNICDetail(String portId) throws QueryNICException {
        try {
            return (NeutronPort)this.osClient.networking().port().get(portId);
        } catch (Exception e) {
            throw new QueryNICException(e.getMessage());
        }
    }

    public void detachNIC(String instanceId, String portId) throws DetachNICException {
        ActionResponse actionResponse ;
        try {
            actionResponse = this.osClient.compute().servers().interfaces().detach(instanceId, portId);
        } catch (Exception e) {
            throw new DetachNICException(e.getMessage());
        }
        if(!actionResponse.isSuccess()){
            throw new DetachNICException(actionResponse.getFault());
        }
    }

    /**
     * @param instanceId   :server的id
     * @param portId：网卡的id
     * @description: Creates and uses a port interface to attach the port to a server instance.
     */
    public void attachNIC(String instanceId, String portId) throws AttachNICException {

        try {
            this.osClient.compute().servers().interfaces().create(instanceId, portId);
        } catch (Exception e) {
            throw new AttachNICException(e.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    public List<NeutronNetwork> getNetworkList() throws QueryNetworkException {

        try {
            return (List<NeutronNetwork>) this.osClient.networking().network().list();
        } catch (Exception e) {
            throw new QueryNetworkException(e.getMessage());
        }
    }

    public Network getNetworkdetail(String networkId) throws QueryNetworkDetailException {
        try {
            return this.osClient.networking().network().get(networkId);
        } catch (Exception e) {
            throw new QueryNetworkDetailException(e.getMessage());
        }
    }

    public Subnet detailSubnet(String subnetId) throws QuerySubnetDetailException {
        try {
            return this.osClient.networking().subnet().get(subnetId);
        } catch (Exception e) {
            throw new QuerySubnetDetailException(e.getMessage());
        }
    }

    /**
     * if (value() == null)
     *            return Collections.emptyList();
     * @return :List<NeutronPort>
     * @throws: QueryPortException
     */
    @SuppressWarnings("unchecked")
    public List<NeutronPort> getPortList() throws QueryPortException {
        try {
            return (List<NeutronPort>) this.osClient.networking().port().list();
        } catch (Exception e) {
            throw new QueryPortException(e.getMessage());
        }
    }
    /**
     * @description: 创建port
     * @param: networkId：the id of the network where this port is associated with
     * @param nicName：端口名称
     * @return: java.lang.String
     */
    public String createNIC(String networkId, String nicName)
            throws CreateNICException {
        try {

            NeutronPort port = (NeutronPort) this.osClient.networking().port()
                    .create(NeutronPort.builder()
                            .name(nicName)
                            .networkId(networkId)
                            .build());
            return port.getId();
        } catch (Exception e) {
            throw new CreateNICException(e.getMessage());
        }
    }

    public void deleteNIC(String portId) throws DeleteNICException {
        ActionResponse actionResponse ;
        try {
            actionResponse = this.osClient.networking().port().delete(portId);
        } catch (Exception e) {
            throw new DeleteNICException(e.getMessage());
        }
        if(!actionResponse.isSuccess()){
            throw new DeleteNICException(actionResponse.getFault());
        }
    }

    //volume
    public void deleteVolume(String volumeId) throws DeleteVolumeException {
        ActionResponse actionResponse;
        try {
            actionResponse = this.osClient.blockStorage().volumes().delete(volumeId);
        } catch (Exception e) {
            throw new DeleteVolumeException(e.getMessage());
        }
        if(!actionResponse.isSuccess()){
            throw new DeleteVolumeException(actionResponse.getFault());
        }
    }

    /**
     * @description: Getting a Volume by ID
     * @param: volumeId 卷的id
     * @return: com.mlcloud.fusioncloud.bean.DetailVolumeResponse
     */
    public CinderVolume getVolumeDetail(String volumeId) throws QueryVolumeException {
        try {
            return (CinderVolume)this.osClient.blockStorage().volumes().get(volumeId);
        } catch (Exception e) {
            throw new QueryVolumeException(e.getMessage());
        }
    }

    /**
     * @param size : 卷大小，单位GB
     * @param volumeType 卷的类型  such as ceph，SSD, SCSI, SATA, Backup, etc.
     * @param volumeName ：卷的名字
     * @return java.lang.String
     * @description: Create a Storage Volume
     */
    public String createEmptyVolume(int size, String volumeType, String volumeName,boolean bootbale)
            throws CreateVolumeException, QueryVolumeException, CreateVolumeTimeoutException {
        String volumeId = "";
        try {
            CinderVolume volume = (CinderVolume)this.osClient.blockStorage().volumes()
                    .create(Builders.volume()
                            .name(volumeName)
                            .size(size)
                            .volumeType(volumeType)
                            .build());
            volumeId = volume.getId();
            //记录新的启动卷
            if(bootbale){
                this.osClient.blockStorage().volumes().bootable(volumeId,true);
            }
        } catch (Exception e) {
            throw new CreateVolumeException(e.getMessage());
        }
        wait4VolumeCreating(volumeId);
        return volumeId;
    }

    private void wait4VolumeCreating(String volumeId)
            throws CreateVolumeTimeoutException, QueryVolumeException {
        CinderVolume detailVolume = this.getVolumeDetail(volumeId);
        int retry = this.getRetryNum(detailVolume.getSize());

        String statusName = detailVolume.getStatus().name();
        while (!AVAILABLE.equals(statusName) && (retry-- > 0)) {
            pause();
            detailVolume = this.getVolumeDetail(volumeId);
            statusName = detailVolume.getStatus().name();
        }
        if (!AVAILABLE.equals(statusName)) {
            throw new CreateVolumeTimeoutException(volumeId);
        }
    }

    /**
     * @param snapshotName
     * @description: 创建快照，
     * 默认force为false，如果卷的状态为in_use的时候会报错。
     * 如果要为in——use状态的卷创建快照，需要设置force为true
     * @param: volumeId
     * @return: java.lang.String
     */
    public String createSnapshot(String volumeId, String snapshotName)
            throws CreateSnapshotException, CreateSnapshotTimeoutException, QuerySnapshotException {
        String snapshotId;
        try {
            CinderVolumeSnapshot snapshot = (CinderVolumeSnapshot)this.osClient.blockStorage().snapshots()
                    .create(Builders.volumeSnapshot()
                            .name(snapshotName)
                            .volume(volumeId)
                            .force(true)
                            .build()
                    );
            snapshotId = snapshot.getId();
        } catch (Exception e) {
            throw new CreateSnapshotException(e.getMessage());
        }
        wait4SnapshotCreating(snapshotId);
        return snapshotId;
    }

    private void wait4SnapshotCreating(String snapshotId)
            throws QuerySnapshotException, CreateSnapshotTimeoutException {
        CinderVolumeSnapshot volumeSnapshot = getVolumeSnapshotDetail(snapshotId);
        int retry = this.getRetryNum(volumeSnapshot.getSize());
        log.info("快照id：{}的大小为：{},重试次数为{}",snapshotId,volumeSnapshot.getSize(),retry);
        String statusName = Optional.ofNullable(volumeSnapshot.getStatus())
                .map(Enum::name)
                .orElse(" ");
        while (!AVAILABLE.equals(statusName) && (retry-- > 0)) {
            pause();
            volumeSnapshot = getVolumeSnapshotDetail(snapshotId);
            statusName = Optional.ofNullable(volumeSnapshot.getStatus())
                    .map(Enum::name)
                    .orElse("");
        }
        if (!AVAILABLE.equals(statusName)) {
            throw new CreateSnapshotTimeoutException(snapshotId);
        }
    }

    /**
     * @param snapshotId ：快照id
     * @return org.openstack4j.model.storage.block.VolumeSnapshot
     * @description:Getting Getting a storage's Snapshot by ID
     */
    public CinderVolumeSnapshot getVolumeSnapshotDetail(String snapshotId)
            throws QuerySnapshotException {
        try {
            return (CinderVolumeSnapshot)this.osClient.blockStorage().snapshots().get(snapshotId);
        } catch (Exception e) {
            throw new QuerySnapshotException(e.getMessage());
        }
    }

    public void deleteSnapshot(String snapshotId) throws DeleteSnapshotException {
        ActionResponse actionResponse;
        try {
            actionResponse = this.osClient.blockStorage().snapshots().delete(snapshotId);
        } catch (Exception e) {
            throw new DeleteSnapshotException(e.getMessage());
        }
        if(!actionResponse.isSuccess()){
            throw new DeleteSnapshotException(actionResponse.getFault());
        }
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
