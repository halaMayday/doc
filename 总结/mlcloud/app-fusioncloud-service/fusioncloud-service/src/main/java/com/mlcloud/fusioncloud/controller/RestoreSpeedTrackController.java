package com.mlcloud.fusioncloud.controller;

import com.mlcloud.cli.anno.Action;
import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.service.SpeedTrackService;
import com.mlcloud.fusioncloud.bean.service.accept.RestoreSpeedTrackParam;
import com.mlcloud.fusioncloud.service.FusioncloudSpeedTrackService;

/**
 * @author ：hf
 * @date ：Created in 2020/12/23 10:18 下午
 */
@Action(name = "restore-speed-track", abbreviation = "RPT", full = "restorespeedtrack", description = "[ACTION]: Track restore speed")
public class RestoreSpeedTrackController extends BaseFusioncloudController {

    @Override
    protected ServiceReturn serviceLogic(ServiceParam serviceParam) throws BaseException {
        RestoreSpeedTrackParam param = (RestoreSpeedTrackParam) serviceParam;
        SpeedTrackService service = new FusioncloudSpeedTrackService(param.getConfigureFilePath(), param.getInstanceId(), param.getGenerationNum());
        return service.trackRestoreSpeed(param);
    }
}
