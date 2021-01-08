package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 10:59 上午
 * @description：
 * @modified By：
 * @version: $
 */
public class DetachVolumeTimeoutException extends BaseFusionCloudException{

    public DetachVolumeTimeoutException(String volumeId) {
        super(String.format("卷%s卸载超时", volumeId), FusionCloudCodeEnum.FUSIONCLOUD_DETACH_VOLUME_TIMEOUT);
    }

}
