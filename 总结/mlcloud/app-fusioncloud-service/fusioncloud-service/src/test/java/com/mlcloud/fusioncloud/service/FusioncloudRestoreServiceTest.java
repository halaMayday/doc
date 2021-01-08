package com.mlcloud.fusioncloud.service;

import com.google.gson.Gson;
import com.mlcloud.fusioncloud.FusinonCloudAccess;
import com.mlcloud.fusioncloud.bean.DetailVolumeResponse;
import com.mlcloud.fusioncloud.bean.InstanceModel;
import com.mlcloud.fusioncloud.bean.OpenRcBean;
import com.mlcloud.fusioncloud.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.openstack.compute.domain.NovaServer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 7:45 下午
 */
@Slf4j
@Ignore
public  class FusioncloudRestoreServiceTest {

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

    private InstanceModel model2Restore;

    @Test
    public void createInstanceTest() {
        String modelStr = "";
        //加载待恢复的实例模型
        this.model2Restore = gson.fromJson(modelStr, InstanceModel.class);

        //todo:测试完可以删除
        if (modelStr.equals(gson.toJson(this.model2Restore))) {
            log.info("json转为实体类的时候没有属性丢失");
        } else {
            log.info("哦豁，gson的属性又丢了。model2Restore的为：{}", gson.toJson(this.model2Restore));
        }
    }

    private NovaServer curInstanceDetail;

    @Test
    public void queryCurInstanceDetailTest() throws QueryInstanceException {
        String instanceId = "";
        this.curInstanceDetail = this.access.getInstanceDetail(instanceId);
    }

    @Test
    public void umountAllVolsTest() throws QueryVolumeException, QueryInstanceException, DetachVolumeException, DetachVolumeTimeoutException {
        queryCurInstanceDetailTest();
        String instanceId = "";

        for (String volumeId : this.curInstanceDetail.getOsExtendedVolumesAttached()) {
            Volume detailVolume = this.access.getVolumeDetail(volumeId);
            //卸载所有非系统硬盘
            if(!detailVolume.bootable()){
                this.access.detachVolume(instanceId,volumeId);
            }
        }
    }

    private Map<String, String> volOld2New = new HashMap<>();

    @Test
    public void createEmptyVolumesTest() throws CreateVolumeException, QueryVolumeException, CreateVolumeTimeoutException {

        createInstanceTest();

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

    @Test
    public void mountNewVolsTest() throws AttachVolumeException, QueryVolumeException, AttachVolumeTimeoutException {
        createInstanceTest();
        String instanceId = "";

        for (DetailVolumeResponse volumeResponse : this.model2Restore.getVolumeDetailList()) {
            boolean bootable = Optional.ofNullable(volumeResponse.getBootable()).orElse(false);
            if(!bootable){
                String volId = volumeResponse.getId();
                String device = volumeResponse.getAttachments().get(0).getDevice();
                log.info("begin to mountNewVols instanceId:{},volId:{},device:{}",instanceId,volId,device);
                this.access.attachVolume(instanceId,volId,device);
            }
        }
    }

    private Map<String, String> src2ImageSpec = new HashMap<>();

    @Test
    public void initImageSpecsTest(){
        //
    }

    @Test
    public void buildRecorveryContextTest(){
        //
    }

    private Map<String, Integer> resTimeoutLimit = new HashMap<>();

    @Test
    public void initResTimeoutLimitTest(){
        createInstanceTest();

        for (DetailVolumeResponse detailVolumeResponse : this.model2Restore.getVolumeDetailList()) {
            int coe = detailVolumeResponse.getSize();
            int timeout = 10 * coe * coe + 600;
            if (timeout < 0 || timeout > 3600 * 24) {
                timeout = 3600 * 24;
            }
            resTimeoutLimit.put(detailVolumeResponse.getId(), timeout);
        }
    }

    @Test
    public void importDataTest(){
        //
    }

    @Test
    public void deleteExpiredVolumesTest() throws QueryInstanceException,
            QueryVolumeException, DeleteVolumeException {

        queryCurInstanceDetailTest();

        for (String volumeId : this.curInstanceDetail.getOsExtendedVolumesAttached()) {
            Volume detailVolume = this.access.getVolumeDetail(volumeId);

            if(!detailVolume.bootable()){
                this.access.deleteVolume(volumeId);
            }
        }
    }

    @Test
    public void recorveryInstanceNetworkTest() throws QueryPortException {
        String instanceId = "";

        String modelStr = "";
        //加载待恢复的实例模型
        this.model2Restore = gson.fromJson(modelStr, InstanceModel.class);

        //  删除当前网卡
        List<String> portIds = getCurInstancePortId();
        portIds.forEach(portId -> {
            try {
                this.access.detachNIC(instanceId, portId);
            } catch (DetachNICException e) {
                log.error(e.getMessage());
            }
        });

        this.model2Restore.getPorts().forEach(port -> port.getFixedIps().forEach(ip -> {
            String networkId = port.getNetworkId();
            String nicName = port.getName();
            String subnetId = ip.getSubnetId();
            String ipAddress = ip.getIpAddress();
            String newPortId = "";
            try {
                newPortId = this.access.createNIC(networkId, nicName);
                this.access.attachNIC(instanceId, newPortId);
                log.info("instance id:{} attach NIC id:{}",instanceId,networkId);
            } catch (CreateNICException e) {
                log.error("新建port id为：{}时发生错误{}", newPortId, e.getMessage());
            } catch (AttachNICException e) {
                log.error("实例:{},绑定网卡:{}时发生错误:{}", instanceId, newPortId, e.getMessage());
            }
        }));
    }

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



}
