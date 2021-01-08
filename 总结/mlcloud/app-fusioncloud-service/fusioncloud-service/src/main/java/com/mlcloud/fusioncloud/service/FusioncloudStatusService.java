package com.mlcloud.fusioncloud.service;


import com.mlcloud.common.exception.BaseException;
import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.bean.ServiceReturn;
import com.mlcloud.defination.exception.LoadConfigurationException;
import com.mlcloud.defination.service.StatusService;
import com.mlcloud.fusioncloud.bean.service.accept.PoweroffParam;
import com.mlcloud.fusioncloud.bean.service.accept.PoweronParam;
import com.mlcloud.fusioncloud.bean.service.accept.QueryStatusParam;
import com.mlcloud.fusioncloud.bean.service.ret.PoweroffReturn;
import com.mlcloud.fusioncloud.bean.service.ret.PoweronReturn;
import com.mlcloud.fusioncloud.bean.service.ret.QueryStatusReturn;
import org.openstack4j.model.compute.Server;

/**
 * @author ：hf
 * @date ：Created in 2020/11/25 5:57 下午
 * @description：
 */
public class FusioncloudStatusService extends BaseFusioncloudService implements StatusService {

    public FusioncloudStatusService(String configFilePath)
            throws LoadConfigurationException {
        super(configFilePath);
    }

    @Override
    public ServiceReturn powerOnVm(ServiceParam serviceParam) throws BaseException {
        PoweronParam param = (PoweronParam) serviceParam;
        this.access.powerOnInstance(param.getInstanceId());
        return new PoweroffReturn();
    }

    @Override
    public ServiceReturn powerOffVm(ServiceParam serviceParam) throws BaseException {
        PoweroffParam param = (PoweroffParam) serviceParam;
        this.access.powerOffInstance(param.getInstanceId());
        return new PoweronReturn();
    }

    @Override
    public ServiceReturn qeuryStatus(ServiceParam serviceParam) throws BaseException {
        QueryStatusParam param = (QueryStatusParam) serviceParam;
        Server instanceModel = this.access.getInstanceDetail(param.getInstanceId());
        QueryStatusReturn ret = new QueryStatusReturn();
        ret.setVmTools(0);
        //todo ：设置ip
        int status;
        switch (instanceModel.getStatus().name()) {
            case "ACTIVE":
                status = 1;
                break;
            case "STOPPED":
            case "SHUTOFF":
                status = 0;
                break;
            default:
                status = 2;
        }
        ret.setPowerStatus(status);
        return ret;
    }

}
