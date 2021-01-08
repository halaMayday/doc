package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 11:22 上午
 * @modified By：
 * @version: $
 */
public class CreateInstanceTimeoutException extends BaseFusionCloudException{

    public CreateInstanceTimeoutException(String instanceId) {
        super(String.format("虚拟机[%s]创建超时", instanceId), FusionCloudCodeEnum.FUSIONCLOUD_CREATE_INSTANCE_TIMEOUT);
    }
}
