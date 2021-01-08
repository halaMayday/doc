package com.mlcloud.fusioncloud.bean.service.ret;

import com.google.gson.annotations.SerializedName;
import com.mlcloud.defination.bean.ServiceReturn;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 10:00 下午
 */
@Data
public final class BackupSpeedTrackReturn implements ServiceReturn {
    /**
     * MB
     */
    private Long read;

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
     * 进度，0~100
     */
    private String progress;
    /**
     * MB
     */
    private String storage;

    /**
     * B
     */
    private String speed;
}
