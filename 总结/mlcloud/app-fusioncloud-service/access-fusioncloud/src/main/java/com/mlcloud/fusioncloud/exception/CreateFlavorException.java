package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 11:22 上午
 * @modified By：
 * @version: $
 */
public class CreateFlavorException extends BaseFusionCloudException{

    public CreateFlavorException(String message) {
        super(message, FusionCloudCodeEnum.FUSIONCLOUD_CREATE_FLAVOR_ERROR);
    }
}
