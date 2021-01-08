package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 10:59 上午
 * @description：
 * @modified By：
 * @version: $
 */
public class PowerOnTimeoutException extends BaseFusionCloudException{

    public PowerOnTimeoutException(String message) {
        super(message, FusionCloudCodeEnum.FUSIONCLOUD_POWERON_INSTANCE_TIMEOUT);
    }

}
