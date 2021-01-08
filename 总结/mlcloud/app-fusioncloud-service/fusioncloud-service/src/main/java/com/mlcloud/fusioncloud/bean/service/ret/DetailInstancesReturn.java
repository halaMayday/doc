package com.mlcloud.fusioncloud.bean.service.ret;

import com.google.gson.annotations.SerializedName;
import com.mlcloud.defination.bean.ServiceReturn;
import lombok.Data;

import java.util.List;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 5:59 下午
 */
@Data
public final class DetailInstancesReturn implements ServiceReturn {

    private Integer count;

    private List<InstanceInfoBean> vminfolist;

    @Data
    public static final class InstanceInfoBean {

        private String vm;

        private String cpu;

        private String mem;

        private String disk;

        @SerializedName("vmpath")
        private String vmPath;

        @SerializedName("guestos")
        private String guestOs;

        @SerializedName("hostname")
        private String hostName;

        private String ip;

        @SerializedName("cpuusage")
        private String cpuUsage;

        @SerializedName( "hostmemusage")
        private String hostMemUsage;

        @SerializedName( "guestmemusage")
        private String guestMemUsage;

        private String overall;

        private String moref;

        @SerializedName( "projectid")
        private String projectId;

        @SerializedName( "projectdomain")
        private String projectDomain;

        @SerializedName( "hostid")
        private String hostId;

        @SerializedName( "hostip")
        private String hostIp;

        @SerializedName("hostuuid")
        private String hostUuid;

        @SerializedName( "vmuuid")
        private String vmUuid;
    }
}
