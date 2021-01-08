package com.mlcloud.fusioncloud.exception;

import com.mlcloud.fusioncloud.code.FusionCloudCodeEnum;

/**
 * @author ：hf
 * @date ：Created in 2020/12/4 10:59 上午
 * @description：
 * @modified By：
 * @version: $
 */
public class QueryPhysicalHostException extends BaseFusionCloudException{

    public QueryPhysicalHostException(String message) {
        super(message, FusionCloudCodeEnum.FUSIONCLOUD_QUERY_PHYSICAL_HOST_ERROR);
    }

}
