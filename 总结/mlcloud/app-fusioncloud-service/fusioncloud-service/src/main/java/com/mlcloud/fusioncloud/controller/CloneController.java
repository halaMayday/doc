package com.mlcloud.fusioncloud.controller;


import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.fusioncloud.bean.service.accept.CloneParam;
import com.mlcloud.fusioncloud.service.FusioncloudCloneService;

/**
 * @author ：hf
 * @date ：Created in 2020/11/24 2:10 下午
 */
@Action(name = "clone", full = "clone", abbreviation = "k", description = "[ACTION]: Clone a instance")
public class CloneController extends BaseFusioncloudController {

    private static final String TASK_NAME = "clone";

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        CloneParam param = (CloneParam)serviceParam;
        FusioncloudCloneService service = new FusioncloudCloneService(
                param.getConfigureFilePath(),
                param.getSrcInstanceId(),
                param.getGenerationNum(),
                param.getLocalhost(),
                TASK_NAME,
                param.getLogId()
        );
        try{
            this.tryLock(service);
            ServiceReturn ret = service.cloneInstance(param);
            this.releaseLock();
            return ret;
        }catch (BaseException e){
            service.rollBack(param);
            throw e;
        }
    }
}
