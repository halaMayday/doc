package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 11:22 上午
 * @modified By：
 * @version: $
 */
public class CreateNICException extends BaseFusionCloudException{

    public CreateNICException(String message) {
        super(message, FusionCloudCodeEnum.FUSIONCLOUD_CREATE_NIC_ERROR);
    }
}
