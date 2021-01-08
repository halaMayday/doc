package com.mlcloud.fusioncloud.bean.service.accept;

import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.mapper.CorrespondTo;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 5:52 下午
 */
@Data
public final class SignalCancelParam implements ServiceParam {

    @CorrespondTo("logId")
    private String logId;

    @CorrespondTo("conf")
    private String configureFilePath;

    @CorrespondTo("instanceId")
    private String instanceId;

}
