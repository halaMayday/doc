package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.fusioncloud.bean.service.accept.RestoreParam;
import com.mlcloud.fusioncloud.service.FusioncloudRestoreService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/14 10:31 上午
 * @description：备份恢复
 * @modified By：
 * @version: $
 */
@Action(name = "restore", abbreviation = "r", full = "restore", description = "[ACTION]: Do a restore operation")
public class RestoreController extends BaseFusioncloudController{

    private static final String TASK_NAME = "restore";

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        RestoreParam param = (RestoreParam) serviceParam;
        FusioncloudRestoreService service = new FusioncloudRestoreService(
                param.getConfigureFilePath(),
                param.getInstanceId(),
                param.getGenerationNum(),
                param.getLocalhost(),
                TASK_NAME,
                param.getLogId()
        );
        try {
            this.tryLock(service);
            ServiceReturn ret = service.restore(param);
            this.releaseLock();
            return ret;
        }catch (BaseException e){
            service.rollBack(param);
            throw e;
        }
    }
}
