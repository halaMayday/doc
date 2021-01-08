package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 11:22 上午
 * @description：
 * @modified By：
 * @version: $
 */
public class CreateVolumeTimeoutException extends BaseFusionCloudException{

    public CreateVolumeTimeoutException(String volumeId) {
        super(String.format("卷[%s]创建超时", volumeId), FusionCloudCodeEnum.FUSIONCLOUD_CREATE_VOLUME_TIMEOUT);
    }
}
