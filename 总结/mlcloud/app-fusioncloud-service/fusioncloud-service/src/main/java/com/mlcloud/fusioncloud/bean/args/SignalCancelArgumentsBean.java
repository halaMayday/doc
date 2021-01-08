package com.mlcloud.fusioncloud.bean.args;

import com.mlcloud.cli.anno.Argument;
import com.mlcloud.cli.anno.Arguments;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.defination.mapper.MapTo;
import com.mlcloud.fusioncloud.bean.service.accept.SignalCancelParam;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 6:18 下午
 */
@Data
@Arguments(action = "cancel")
@MapTo(SignalCancelParam.class)
public class SignalCancelArgumentsBean extends ArgumentsBean {

    @Argument(abbreviation = "c", full = "conf", hasArgument = true, required = true, argumentName = "file name", description = "[ARGUMENT]: Platform configuration file")
    private String conf;

    @Argument(abbreviation = "m", full = "moref", hasArgument = true, required = true, argumentName = "id", description = "[ARGUMENT]: UUID of source virtual machine")
    private String instanceId;

    @Argument(abbreviation = "logId", full = "log-id", hasArgument = true, required = true, argumentName = "log id", description = "[ARGUMENT]: This is used for passing log informations to redis")
    private String logId;



}
