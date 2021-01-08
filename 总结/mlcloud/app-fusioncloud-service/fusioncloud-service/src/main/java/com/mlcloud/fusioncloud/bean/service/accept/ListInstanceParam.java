package com.mlcloud.fusioncloud.bean.service.accept;

import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.mapper.CorrespondTo;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 6:07 下午
 */
@Data
public final class ListInstanceParam implements ServiceParam {

    @CorrespondTo("conf")
    private String configurationFilePath;
}
