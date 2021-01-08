package com.mlcloud.fusioncloud.bean.service.accept;

import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.mapper.CorrespondTo;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 6:12 下午
 */
@Data
public final class RestoreSpeedTrackParam implements ServiceParam {

    @CorrespondTo("instanceId")
    private String instanceId;

    @CorrespondTo("generation")
    private String generationNum;

    @CorrespondTo("conf")
    private String configureFilePath;

}
