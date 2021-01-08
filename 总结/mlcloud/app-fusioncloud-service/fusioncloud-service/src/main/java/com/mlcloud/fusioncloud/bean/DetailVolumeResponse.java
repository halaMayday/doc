package com.mlcloud.fusioncloud.bean;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.openstack.storage.block.domain.CinderVolumeAttachment;

import java.util.List;

/**
 * @author ：hf
 * @date ：Created in 2020/12/8 12:51
 * @modified By：
 * @version: $
 */
@Data
@Builder
public class DetailVolumeResponse {
    /**
     * 卷Id
     */
    private String id;
    /**
     * 硬盘储存类型，eg：ceph
     */
    private String volumeType;
    /**
     * 硬盘大小，单位：GB
     */
    private Integer size;
    /**
     * 卷的名字
     */
    private String name;
    /**
     * 磁盘状态
     */
    private Volume.Status status;
    /**
     *
     * 卷挂载设备名称 eg:/dev/vda
     */
    private List<CinderVolumeAttachment> attachments;
    /**
     * 是否可以启动
     */
    private Boolean bootable;
    /**
     * 储存池名称：eg：openstack对接ceph的配置为 rbd_pool = volumes
     */
    private String poolName;

    @Tolerate
    DetailVolumeResponse(){
    }


}
