package com.mlcloud.fusioncloud.bean.service.accept;

import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.mapper.CorrespondTo;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/11/24 2:07 下午
 * @modified By：
 * @version: $
 */
@Data
public final class BackupParam implements ServiceParam {

    @CorrespondTo("conf")
    private String configureFilePath;

    @CorrespondTo("instanceId")
    private String instanceId;

    @CorrespondTo("generation")
    private String generationNum;

    @CorrespondTo("isFull")
    private Boolean isFull;

    @CorrespondTo("host")
    private String localhost;

    @CorrespondTo("logId")
    private String logId;

}
