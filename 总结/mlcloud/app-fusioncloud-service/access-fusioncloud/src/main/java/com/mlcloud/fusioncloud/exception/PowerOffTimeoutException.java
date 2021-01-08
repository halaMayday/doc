package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 10:59 上午
 * @description：
 * @modified By：
 * @version: $
 */
public class PowerOffTimeoutException extends BaseFusionCloudException{

    public PowerOffTimeoutException(String instanceId) {
        super(String.format("虚拟机[%s]关机超时", instanceId), FusionCloudCodeEnum.FUSIONCLOUD_POWER_OFF_INSTANCE_TIMEOUT);
    }

}
