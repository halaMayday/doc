package com.mlcloud.fusioncloud.service;


import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.exception.LoadConfigurationException;
import com.mlcloud.defination.service.ConnectService;
import com.mlcloud.fusioncloud.bean.service.ret.QueryConnectionReturn;

/**
 * @author ：hf
 * @date ：Created in 2020/11/25 5:45 下午
 * @description：
 * @modified By：
 * @version: $
 */
public class FusioncloudConnectService extends BaseFusioncloudService implements ConnectService {

    public FusioncloudConnectService(String configFilePath)
            throws LoadConfigurationException {
        super(configFilePath);
    }

    @Override
    public ServiceReturn connectionQuery(ServiceParam serviceParam) throws BaseException {
        return new QueryConnectionReturn();
    }
}
