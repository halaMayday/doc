package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 11:22 上午
 * @description：
 * @modified By：
 * @version: $
 */
public class CreateSnapshotTimeoutException extends BaseFusionCloudException{

    public CreateSnapshotTimeoutException(String snapshotId) {
        super(String.format("快照%s创建超时", snapshotId), FusionCloudCodeEnum.FUSIONCLOUD_CREATE_SNAPSHOT_TIMEOUT);
    }
}
