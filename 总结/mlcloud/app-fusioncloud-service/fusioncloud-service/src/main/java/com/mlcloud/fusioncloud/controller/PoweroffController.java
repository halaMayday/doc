package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.service.StatusService;
import com.mlcloud.fusioncloud.bean.service.accept.PoweroffParam;
import com.mlcloud.fusioncloud.service.FusioncloudStatusService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 11:22 下午
 */
@Action(name = "power-off-vm", abbreviation = "sdvm", full = "shutdown-vm", description = "[ACTION]: Power off a VM")
public class PoweroffController extends BaseFusioncloudController{

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        PoweroffParam param = (PoweroffParam)serviceParam;
        StatusService service = new FusioncloudStatusService(param.getConfigureFilePath());
        return service.powerOffVm(param);
    }


}
