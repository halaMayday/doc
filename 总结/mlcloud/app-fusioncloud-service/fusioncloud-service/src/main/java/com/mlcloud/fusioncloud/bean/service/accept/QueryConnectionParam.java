package com.mlcloud.fusioncloud.bean.service.accept;

import com.mlcloud.defination.bean.ServiceParam;
import com.mlcloud.defination.mapper.CorrespondTo;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 6:10 下午
 */
@Data
public final class QueryConnectionParam implements ServiceParam {

    @CorrespondTo("conf")
    private String configureFilePath;

}
