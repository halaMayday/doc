package com.mlcloud.fusioncloud.bean;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.openstack4j.model.compute.Server;
import org.openstack4j.openstack.compute.domain.NovaAddresses;
import org.openstack4j.openstack.compute.domain.NovaSecurityGroup;

import java.util.List;

/**
 * @author ：hf
 * @date ：Created in 2020/12/16 9:46 下午
 * @description：
 * @modified By：
 * @version: $
 */
@Data
@Builder
public class DetailInstanceResponse  {

    private String id;

    /**
     * 云主机名称
     */
    private String name;

    /**
     * 项目id
     */
    private String tenantId;

    /**
     * 物理主机名称
     */
    private String host;

    /**
     * 物理主机id
     */
    private String hostId;

    /**
     * 可用区域
     */
    private String availabilityZone;

    /**
     * 镜像id
     */
    private String imageId;

    private NovaAddresses addresses;

    private Server.Status status;

    private String powerState;

    /**
     * 挂载磁盘id
     */
    private List<String> osExtendedVolumesAttached;

    /**
     * 安全组
     */
    private List<NovaSecurityGroup> securityGroups;

    /**
     * 规格模板id
     */
    private String flavorId;

    @Tolerate
    DetailInstanceResponse(){}
}
