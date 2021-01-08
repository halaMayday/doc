package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 11:22 上午
 * @description：
 * @modified By：
 * @version: $
 */
public class CreateSnapshotException extends BaseFusionCloudException{

    public CreateSnapshotException(String message) {
        super(message, FusionCloudCodeEnum.FUSIONCLOUD_CREATE_SNAPSHOT_ERROR);
    }
}
