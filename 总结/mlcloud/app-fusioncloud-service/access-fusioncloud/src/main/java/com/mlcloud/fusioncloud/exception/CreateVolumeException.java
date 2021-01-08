package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 11:22 上午
 * @description：
 * @modified By：
 * @version: $
 */
public class CreateVolumeException extends BaseFusionCloudException{

    public CreateVolumeException(String message) {
        super(message, FusionCloudCodeEnum.FUSIONCLOUD_CREATE_VOLUME_ERROR);
    }
}
