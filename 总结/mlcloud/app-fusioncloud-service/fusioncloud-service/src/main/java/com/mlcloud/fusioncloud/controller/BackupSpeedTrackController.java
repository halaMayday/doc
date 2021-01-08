package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.service.SpeedTrackService;
import com.mlcloud.fusioncloud.bean.service.accept.BackupSpeedTrackParam;
import com.mlcloud.fusioncloud.service.FusioncloudSpeedTrackService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/21 10:17 下午
 */
@Action(name = "backup-speed-track", abbreviation = "T", full = "speedtrack", description = "[ACTION]: Track backup speed")
public class BackupSpeedTrackController extends BaseFusioncloudController {

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        BackupSpeedTrackParam param = (BackupSpeedTrackParam) serviceParam;
        SpeedTrackService service = new FusioncloudSpeedTrackService(param.getConfigureFilePath(), param.getInstanceId());
        return service.trackBackupSpeed(param);
    }
}
