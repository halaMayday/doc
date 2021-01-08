package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 10:59 上午
 * @modified By：
 * @version: $
 */
public class AttachNICException extends BaseFusionCloudException{

    public AttachNICException(String message) {
        super(message, FusionCloudCodeEnum.FUSIONCLOUD_ATTACH_NIC_ERROR);
    }

}
