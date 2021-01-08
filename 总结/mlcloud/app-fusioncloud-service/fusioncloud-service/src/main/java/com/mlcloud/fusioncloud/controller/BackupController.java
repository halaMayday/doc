package com.mlcloud.fusioncloud.controller;

import com.mlcloud.fusioncloud.bean.service.accept.BackupParam;
import com.mlcloud.fusioncloud.service.FusioncloudBackupService;
import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;

/**
 * @author ：hf
 * @date ：Created in 2020/11/24 2:07 下午
 * @modified By：
 * @version: $
 */
@Action(name = "backup",abbreviation = "b",full = "backup",description = "[ACTION:] Do a back operation")
public class BackupController extends BaseFusioncloudController {

    private static final String TASK_NAME = "backup";

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        BackupParam param = (BackupParam)serviceParam;
        FusioncloudBackupService service = new FusioncloudBackupService(
                param.getConfigureFilePath(),
                param.getInstanceId(),
                param.getGenerationNum(),
                param.getLocalhost(),
                TASK_NAME,
                param.getLogId()
        );
        try {
            this.tryLock(service);
            ServiceReturn ret = service.backup(param);
            this.releaseLock();
            return ret;
        }catch (BaseException e){
            service.rollBack(param);
            throw e;
        }
    }
}
