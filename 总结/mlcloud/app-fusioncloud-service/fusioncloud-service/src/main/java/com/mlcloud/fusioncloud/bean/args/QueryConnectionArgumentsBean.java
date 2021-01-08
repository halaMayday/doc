package com.mlcloud.fusioncloud.bean.args;

import com.mlcloud.cli.anno.Argument;
import com.mlcloud.cli.anno.Arguments;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.defination.mapper.MapTo;
import com.mlcloud.fusioncloud.bean.service.accept.QueryConnectionParam;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 6:10 下午
 */
@Data
@Arguments(action = "connection-query")
@MapTo(QueryConnectionParam.class)
public class QueryConnectionArgumentsBean extends ArgumentsBean {

    @Argument(abbreviation = "c", full = "conf", hasArgument = true, required = true, argumentName = "file name", description = "[ARGUMENT]: Platform configuration file")
    private String conf;

    /**
     * 兼容项
     */
    @Argument(abbreviation = "VI", full = "vcenterip", hasArgument = true)
    private String vcenterIp;

    /**
     * 兼容项
     */
    @Argument(abbreviation = "VU", full = "vcenteruser")
    private Boolean vcenterUser;

    /**
     * 兼容项
     */
    @Argument(abbreviation = "VP", full = "vcenterpass")
    private Boolean vcenterPasswd;
}
