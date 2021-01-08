package com.mlcloud.fusioncloud.bean.service.ret;

import com.google.gson.annotations.SerializedName;
import com.mlcloud.defination.bean.ServiceReturn;
import lombok.Data;

import java.util.List;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 11:15 下午
 */
@Data
public final class ListHostReturn implements ServiceReturn {

    private Integer count;

    private String version = "V";

    private List<HostInfoBean> hostlist;

    @Data
    public static class HostInfoBean{

        private String ip;

        @SerializedName("hostuuid")
        private String hostUuid;

        /**
         * "normal" : "abnormal"
         */
        private String hostStatus;
    }

}
