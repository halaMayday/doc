package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.service.ListService;
import com.mlcloud.fusioncloud.bean.service.accept.ListInstanceParam;
import com.mlcloud.fusioncloud.service.FusioncloudListService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 11:20 下午
 */
@Action(name = "get-instances-list", abbreviation = "l", full = "discovery", description = "[ACTION]: Get instances' list")
public class ListInstanceController extends BaseFusioncloudController{

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        ListInstanceParam param = (ListInstanceParam)serviceParam;
        ListService service = new FusioncloudListService(param.getConfigurationFilePath());
        return service.getInstancesList(param);
    }
}
