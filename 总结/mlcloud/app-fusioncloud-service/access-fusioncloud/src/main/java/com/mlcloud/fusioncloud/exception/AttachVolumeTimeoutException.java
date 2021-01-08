package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 10:59 上午
 * @modified By：
 * @version: $
 */
public class AttachVolumeTimeoutException extends BaseFusionCloudException{

    public AttachVolumeTimeoutException(String volumeId) {
        super(String.format("卷%s挂载超时", volumeId), FusionCloudCodeEnum.FUSIONCLOUD_ATTACH_VOLUME_TIMEOUT);
    }

}
