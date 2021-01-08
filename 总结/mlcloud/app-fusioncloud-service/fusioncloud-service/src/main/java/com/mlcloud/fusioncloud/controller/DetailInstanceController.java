package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.service.ListService;
import com.mlcloud.fusioncloud.bean.service.accept.DetailInstancesParam;
import com.mlcloud.fusioncloud.service.FusioncloudListService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 5:54 下午
 */
@Action(name = "get-instances-detail", abbreviation = "i", full = "info", description = "[ACTION]: Get instances' detail")
public class DetailInstanceController extends BaseFusioncloudController{

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        DetailInstancesParam param = (DetailInstancesParam)serviceParam;
        ListService service = new FusioncloudListService(param.getConfigurationFilePath());
        return service.getInstancesDetailList(param);
    }
}
