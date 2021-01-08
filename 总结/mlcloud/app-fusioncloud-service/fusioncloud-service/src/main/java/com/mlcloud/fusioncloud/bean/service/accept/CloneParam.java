package com.mlcloud.fusioncloud.bean.service.accept;

import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.mapper.CorrespondTo;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/17 10:42 上午
 */
@Data
public final class CloneParam implements ServiceParam {

    @CorrespondTo("conf")
    private String configureFilePath;

    @CorrespondTo("host")
    private String localhost;

    @CorrespondTo("sourceInstanceId")
    private String srcInstanceId;

    @CorrespondTo("destInstanceName")
    private String destInstancceName;

    @CorrespondTo("generation")
    private String generationNum;


    @CorrespondTo("logId")
    private String logId;
    @CorrespondTo("powerOn")
    private Boolean powerOn;

    @CorrespondTo("accessNetwork")
    private Boolean networkAccess;


}
