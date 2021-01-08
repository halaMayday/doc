package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.service.ListService;
import com.mlcloud.fusioncloud.bean.service.accept.ListHostParam;
import com.mlcloud.fusioncloud.service.FusioncloudListService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 6:03 下午
 */
//need work
@Action(name = "api-localhost-query", abbreviation = "f", full = "listhost", description = "[ACTION]: Query avaliable agent apiHost")
public class ListHostController extends BaseFusioncloudController{

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        ListHostParam param = (ListHostParam)serviceParam;
        ListService service = new FusioncloudListService(param.getConfigureFilePath());
        return service.getHostList(param);
    }
}
