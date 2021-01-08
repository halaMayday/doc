package com.mlcloud.fusioncloud.service;


import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.exception.LoadConfigurationException;
import com.mlcloud.defination.service.SignalService;
import com.mlcloud.defination.util.SignalUtil;
import com.mlcloud.fusioncloud.bean.service.accept.SignalCancelParam;
import com.mlcloud.fusioncloud.bean.service.ret.SignalCancelReturn;
import com.mlcloud.local.exception.os.LocalReadException;

/**
 * @author ：hf
 * @date ：Created in 2020/11/25 5:55 下午
 * @description：
 * @modified By：
 * @version: $
 */
public class FusioncloudSignalService extends BaseFusioncloudService implements SignalService {

    public FusioncloudSignalService(String configFilePath, String instanceId)
            throws LoadConfigurationException, LocalReadException {
        super(configFilePath, instanceId);
    }

    @Override
    public ServiceReturn signalCancel(ServiceParam serviceParam) throws BaseException {
        SignalCancelParam param = (SignalCancelParam)serviceParam;
        SignalUtil.signalCancel(param.getLogId(), this.getSignalFile(), this.getSignalFileLock());
        return new SignalCancelReturn();
    }
}
