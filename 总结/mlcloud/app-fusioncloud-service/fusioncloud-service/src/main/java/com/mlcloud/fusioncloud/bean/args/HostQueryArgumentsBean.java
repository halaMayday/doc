package com.mlcloud.fusioncloud.bean.args;

import com.mlcloud.cli.anno.Argument;
import com.mlcloud.cli.anno.Arguments;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.defination.mapper.MapTo;
import com.mlcloud.fusioncloud.bean.service.accept.ListHostParam;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 6:05 下午
 */
@Data
@Arguments(action = "api-localhost-query")
@MapTo(ListHostParam.class)
public class HostQueryArgumentsBean extends ArgumentsBean {

    @Argument(abbreviation = "c", full = "conf", hasArgument = true, required = true, argumentName = "file name", description = "[ARGUMENT]: Platform configuration file")
    private String conf;

}
