package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 10:59 上午
 * @modified By：
 * @version: $
 */
public class AttachVolumeException extends BaseFusionCloudException{

    public AttachVolumeException(String message) {
        super(message, FusionCloudCodeEnum.FUSIONCLOUD_ATTACH_VOLUME_ERROR);
    }

}
