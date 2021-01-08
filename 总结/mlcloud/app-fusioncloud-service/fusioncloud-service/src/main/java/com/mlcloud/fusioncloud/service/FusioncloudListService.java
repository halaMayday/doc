package com.mlcloud.fusioncloud.service;


import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.exception.LoadConfigurationException;
import com.mlcloud.defination.service.ListService;
import com.mlcloud.fusioncloud.bean.service.ret.DetailInstancesReturn;
import com.mlcloud.fusioncloud.bean.service.ret.ListHostReturn;
import com.mlcloud.fusioncloud.bean.service.ret.ListInstanceReturn;
import org.openstack4j.model.compute.Image;
import org.openstack4j.openstack.compute.domain.NovaServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ：hf
 * @date ：Created in 2020/11/25 5:53 下午
 * @description：
 * @modified By：
 * @version: $
 */
public class FusioncloudListService extends BaseFusioncloudService implements ListService {

    public FusioncloudListService(String configFilePath) throws LoadConfigurationException {
        super(configFilePath);
    }

    //todo : check  setMoref  setInstanceId
    @Override
    public ServiceReturn getInstancesList(ServiceParam serviceParam) throws BaseException {
        List<ListInstanceReturn.InstanceInfoBean> infoList = new ArrayList<>();
        List<NovaServer> servers = this.access.getInstanceList();
        for (NovaServer server : servers) {

            int totalSize = 0;
            for (String volumeId : server.getOsExtendedVolumesAttached()) {
                totalSize += this.access.getVolumeDetail(volumeId).getSize();
            }
            ListInstanceReturn.InstanceInfoBean info = new ListInstanceReturn.InstanceInfoBean();
            info.setDc(server.getHost());
            info.setMoref(server.getId());
            info.setSize(Integer.toString(totalSize));
            info.setPowerState(server.getPowerState());
            info.setVm(server.getName());
            info.setInstanceId(server.getId());
            info.setVmTools("0");
            infoList.add(info);
            info.setGroup(server.getHost());
        }
        ListInstanceReturn ret = new ListInstanceReturn();
        ret.setVmList(infoList);
        ret.setCount(infoList.size());
        return ret;
    }

    @Override
    public ServiceReturn getInstancesDetailList(ServiceParam serviceParam) throws BaseException {
        List<String> instanceIds = this.access.getInstanceList().stream()
                .map(NovaServer::getId)
                .collect(Collectors.toList());
        List<DetailInstancesReturn.InstanceInfoBean> infoList = new ArrayList<>();
        for (String instanceId : instanceIds) {
            NovaServer instance = this.access.getInstanceDetail(instanceId);
//            ExtHypervisor hypervisor = this.access.getPhysicalHostList().get(0);
            DetailInstancesReturn.InstanceInfoBean info = new DetailInstancesReturn.InstanceInfoBean();
            info.setMoref(instanceId);
            info.setVm(instance.getName());
            info.setCpu(Integer.toString(instance.getFlavor().getVcpus()));
            info.setVmPath(instance.getHost());
            info.setHostName(instance.getHost());
            info.setHostUuid(instance.getHostId());
            //以块存储创建主机，Image为null
            String imageName = Optional.ofNullable(instance.getImage()).map(Image::getName).orElse("");
            info.setGuestOs(imageName);
            //todo : cpu 使用率
//            info.setCpuUsage(Float.toString(((float) hypervisor.getVirtualUsedCPU()) / (float) hypervisor.getVirtualCPU()));
            info.setCpuUsage("0.10");
            info.setMem(Integer.toString(instance.getFlavor().getRam()));
            // int freeRam = hypervisor.getFreeRam();
            //todo :ram 使用率
            info.setGuestMemUsage("0.10");
            info.setDisk(Integer.toString(instance.getOsExtendedVolumesAttached().size()));
            info.setVmUuid(instanceId);
            info.setOverall("0");

            //todo：设置私网和公网ip
            infoList.add(info);
        }
        DetailInstancesReturn ret = new DetailInstancesReturn();
        ret.setCount(infoList.size());
        ret.setVminfolist(infoList);
        return ret;
    }

    @Override
    public ServiceReturn getHostList(ServiceParam serviceParam) throws BaseException {
        List<ListHostReturn.HostInfoBean> hostInfoList = this.access.getPhysicalHostList().stream()
                .map(host -> {
                    ListHostReturn.HostInfoBean hostInfo = new ListHostReturn.HostInfoBean();
                    hostInfo.setHostStatus("normal");
                    hostInfo.setHostUuid(host.getId());
                    hostInfo.setIp(host.getHostIP());
                    return hostInfo;
                })
                .collect(Collectors.toList());

        ListHostReturn ret = new ListHostReturn();
        ret.setCount(hostInfoList.size());
        ret.setHostlist(hostInfoList);
        ret.setVersion("V");
        return ret;
    }

}
