package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.service.SignalService;
import com.mlcloud.fusioncloud.bean.service.accept.SignalCancelParam;
import com.mlcloud.fusioncloud.service.FusioncloudSignalService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 11:28 下午
 */
@Action(name = "cancel", full = "cancel", description = "[ACTION] Make a task cancelation signal")
public class SignalCancelController extends BaseFusioncloudController{

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        SignalCancelParam param = (SignalCancelParam) serviceParam;
        SignalService service = new FusioncloudSignalService(param.getConfigureFilePath(), param.getInstanceId());
        return service.signalCancel(param);
    }
}
