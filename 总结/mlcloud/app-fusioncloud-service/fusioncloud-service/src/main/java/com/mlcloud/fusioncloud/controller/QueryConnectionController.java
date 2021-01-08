package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.service.ConnectService;
import com.mlcloud.fusioncloud.bean.service.accept.QueryConnectionParam;
import com.mlcloud.fusioncloud.service.FusioncloudConnectService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 11:26 下午
 */
@Action(name = "connection-query", full = "connect", abbreviation = "C", description = "[ACTION]: Query whether a platform is connected or not")
public class QueryConnectionController extends BaseFusioncloudController{

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        QueryConnectionParam param = (QueryConnectionParam)serviceParam;
        ConnectService service = new FusioncloudConnectService(param.getConfigureFilePath());
        return service.connectionQuery(param);
    }

}
