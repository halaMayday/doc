package com.mlcloud.fusioncloud.bean.service.ret;

import com.google.gson.annotations.SerializedName;
import com.mlcloud.defination.bean.ServiceReturn;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 11:35 下午
 */
@Data
public final class QueryStatusReturn implements ServiceReturn {

   @SerializedName("powerstatus")
    private Integer powerStatus;

    private String ipAddress;

    @SerializedName("vmtools")
    private Integer vmTools;

}
