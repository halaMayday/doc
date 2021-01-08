package com.mlcloud.fusioncloud.bean.service.ret;

import com.google.gson.annotations.SerializedName;
import com.mlcloud.defination.bean.ServiceReturn;
import lombok.Data;

import java.util.List;

/**
 * @author ：hf
 * @date ：Created in 2020/12/22 10:23 下午
 */
@Data
public final class ListInstanceReturn implements ServiceReturn {

    private Integer count;

    @SerializedName("vmlist")
    private List<InstanceInfoBean> vmList;

    @Data
    public static final class InstanceInfoBean {

        @SerializedName( "dc")
        private String dc;

        private String folder;

        private String group;

        private String moref;

        private String size;

        @SerializedName( "powerstate")
        private String powerState;

        private String vm;

        @SerializedName( "vmtools")
        private String vmTools;

        @SerializedName( "vmuuid")
        private String instanceId;
    }
}
