package com.mlcloud.fusioncloud.bean.args;

import com.mlcloud.cli.anno.Argument;
import com.mlcloud.cli.anno.Arguments;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.defination.mapper.MapTo;
import com.mlcloud.fusioncloud.bean.service.accept.DetailInstancesParam;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 5:58 下午
 */
@Data
@Arguments(action = "get-instances-detail")
@MapTo(DetailInstancesParam.class)
public class DetailInstancesArgumentsBean extends ArgumentsBean {

    @Argument(abbreviation = "c", full = "conf", hasArgument = true, required = true, argumentName = "file name", description = "[ARGUMENT]: Platform configuration file")
    private String conf;

    /**
     * 兼容项
     */
    @Argument(abbreviation = "VP", full = "vcenterpass", hasArgument = true)
    private String vcenterPasswd;
}
