package com.mlcloud.fusioncloud.bean.service.ret;

import com.google.gson.annotations.SerializedName;
import com.mlcloud.defination.bean.ServiceReturn;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 10:13 下午
 */
@Data
public final class RestoreSpeedTrackReturn implements ServiceReturn {

    /**
     * MB
     */
    private Long write;

    /**
     * MB
     */
    private Long size;

    /**
     * vm id
     */
    private String vm;

    /**
     * B
     */
    @SerializedName("avgspeed")
    private String avgSpeed;

    /**
     * int 0~100
     */
    private String progress;

    /**
     * MB
     */
    private String storage;

    /**
     *B
     */
    private String speed;
}
