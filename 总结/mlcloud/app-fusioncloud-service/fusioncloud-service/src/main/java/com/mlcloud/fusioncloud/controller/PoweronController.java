package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.service.StatusService;
import com.mlcloud.fusioncloud.bean.service.accept.PoweronParam;
import com.mlcloud.fusioncloud.service.FusioncloudStatusService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 11:22 下午
 */
@Action(name = "power-on-vm", abbreviation = "pon", full = "power-on", description = "[ACTION]: Power on a VM")
public class PoweronController extends BaseFusioncloudController{

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        PoweronParam param = (PoweronParam)serviceParam;
        StatusService service = new FusioncloudStatusService(param.getConfigureFilePath());
        return service.powerOnVm(param);
    }



}
