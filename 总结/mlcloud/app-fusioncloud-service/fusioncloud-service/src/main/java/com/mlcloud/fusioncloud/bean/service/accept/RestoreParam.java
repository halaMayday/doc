package com.mlcloud.fusioncloud.bean.service.accept;

import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.mapper.CorrespondTo;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/14 1:42 下午
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public final class RestoreParam implements ServiceParam {

    @CorrespondTo("conf")
    private String configureFilePath;

    @CorrespondTo("instanceId")
    private String instanceId;

    @CorrespondTo("host")
    private String localhost;

    @CorrespondTo("generation")
    private String generationNum;

    @CorrespondTo("logId")
    private String logId;

    @CorrespondTo("powerOn")
    private Boolean powerOn;

}
