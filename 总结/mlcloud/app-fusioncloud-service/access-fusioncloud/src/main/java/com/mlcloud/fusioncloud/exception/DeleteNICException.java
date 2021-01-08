package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 10:59 上午
 * @description：
 * @modified By：
 * @version: $
 */
public class DeleteNICException extends BaseFusionCloudException{

    public DeleteNICException(String message) {
        super(message, FusionCloudCodeEnum.FUSIONCLOUD_DELETE_NIC_ERROR);
    }

}
