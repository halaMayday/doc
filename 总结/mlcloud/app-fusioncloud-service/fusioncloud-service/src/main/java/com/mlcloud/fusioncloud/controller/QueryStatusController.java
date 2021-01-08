package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.service.StatusService;
import com.mlcloud.fusioncloud.bean.service.accept.QueryStatusParam;
import com.mlcloud.fusioncloud.service.FusioncloudStatusService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 11:27 下午
 */
@Action(name = "vm-status-query", abbreviation = "cv", full = "check-vm", description = "[ACTION]: Query VM status")
public class QueryStatusController extends BaseFusioncloudController{

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        QueryStatusParam param = (QueryStatusParam) serviceParam;
        StatusService service = new FusioncloudStatusService(param.getConfigureFilePath());
        return service.qeuryStatus(param);
    }


}
