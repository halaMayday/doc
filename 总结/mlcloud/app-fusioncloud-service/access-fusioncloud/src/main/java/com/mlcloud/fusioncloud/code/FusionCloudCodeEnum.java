package com.mlcloud.fusioncloud.code;


import com.mlcloud.common.code.CodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 11:01 上午
 */
public enum FusionCloudCodeEnum implements CodeEnum {

    //错误码
    FUSIONCLOUD_QUERY_INSTANCE_ERROR("查询虚拟机失败",1),

    FUSIONCLOUD_CREATE_INSTANCE_ERROR("创建虚拟机失败",1 ),

    FUSIONCLOUD_CREATE_INSTANCE_TIMEOUT("创建虚拟机超时",1 ),

    FUSIONCLOUD_DELETE_INSTANCE_ERROR("删除虚拟机失败",1 ),

    FUSIONCLOUD_FORCE_DELETE_INSTANCE_ERROR("从回收站删除虚拟机失败",1 ),

    FUSIONCLOUD_POWEROFF_INSTANCE_ERROR("虚拟机关机失败",1 ),

    FUSIONCLOUD_POWERON_INSTANCE_ERROR("虚拟机开机失败",1 ),

    FUSIONCLOUD_POWER_OFF_INSTANCE_TIMEOUT("关机超时",1),

    FUSIONCLOUD_POWERON_INSTANCE_TIMEOUT("开机超时", 1),

    FUSIONCLOUD_QUERY_VOLUME_ERROR("卷查询失败",1 ),

    FUSIONCLOUD_ATTACH_VOLUME_ERROR("挂载卷失败", 1),

    FUSIONCLOUD_ATTACH_VOLUME_TIMEOUT("挂载卷超时", 1),

    FUSIONCLOUD_DETACH_VOLUME_ERROR("卸载卷失败", 1),

    FUSIONCLOUD_DETACH_VOLUME_TIMEOUT("卸载卷超时", 1),

    FUSIONCLOUD_DELETE_VOLUME_ERROR("删除卷失败", 1),

    FUSIONCLOUD_CREATE_VOLUME_TIMEOUT("创建卷超时",1 ),

    FUSIONCLOUD_CREATE_VOLUME_ERROR("创建卷失败",1 ),

    FUSIONCLOUD_CREATE_NIC_ERROR("创建网卡失败",1 ),

    FUSIONCLOUD_ATTACH_NIC_ERROR("挂载网卡失败",1 ),

    FUSIONCLOUD_DETACH_NIC_ERROR("卸载网卡失败", 1),

    FUSIONCLOUD_DELETE_NIC_ERROR("删除网卡失败", 1),

    FUSIONCLOUD_QUERY_PORT_ERROR("查询网卡失败", 1),

    FUSIONCLOUD_QUERY_PHYSICAL_HOST_ERROR("物理主机查询失败",1 ),

    FUSIONCLOUD_CREATE_FLAVOR_ERROR("创建规格失败",1 ),

    FUSIONCLOUD_DELETE_FLAVOR_ERROR("删除规格失败", 1),

    FUSIONCLOUD_CREATE_SNAPSHOT_ERROR("创建快照失败",1 ),

    FUSIONCLOUD_QUERY_SNAPSHOT_ERROR("查询快照失败",1 ),

    FUSIONCLOUD_CREATE_SNAPSHOT_TIMEOUT("创建快照超时",1 ),

    FUSIONCLOUD_DELETE_SNAPSHOT_ERROR("删除快照失败", 1),

    FUSIONCLOUD_QUERY_NEWORK_ERROR("查询网络总览失败",1 ),

    FUSIONCLOUD_QUERY_PORTLIST_ERROR("查询网卡总览失败",1 ),

    FUSIONCLOUD_QUERY_NEWORK_DETAIL_ERROR("查询网络信息失败", 1),

    FUSIONCLOUD_QUERY_SUBNET_DETAIL_ERROR("查询子网详情失败", 1);



    private String message;

    private int code;

    FusionCloudCodeEnum(String message, int code){
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
