package com.mlcloud.fusioncloud.bean.args;

import com.mlcloud.cli.anno.Argument;
import com.mlcloud.cli.anno.Arguments;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.defination.mapper.MapTo;
import com.mlcloud.fusioncloud.bean.service.accept.CloneParam;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/17 10:41 上午
 */
@Data
@Arguments(action = "clone")
@MapTo(CloneParam.class)
public class CloneArgumentsBean extends ArgumentsBean {

    @Argument(abbreviation = "c", full = "conf", hasArgument = true, required = true, argumentName = "file name", description = "[ARGUMENT]: Platform configuration file")
    private String conf;

    @Argument(abbreviation = "H", full = "localhost", hasArgument = true, required = true, description = "[ARGUMENT]: IP address of code apiHost")
    private String host;

    @Argument(abbreviation = "m", full = "moref", hasArgument = true, required = true, description = "[ARGUMENT]: Id of Source instance")
    private String sourceInstanceId;

    @Argument(abbreviation = "V", full = "newvm", hasArgument = true, required = true, description = "[ARGUMENT]: Instance name of clone instance")
    private String destInstanceName;

    @Argument(abbreviation = "g", full = "generationNum", hasArgument = true, description = "[ARGUMENT]: Clone generationNum")
    private String generation;

    @Argument(abbreviation = "logId", full = "log-id", hasArgument = true, argumentName = "log id", description = "[ARGUMENT]: This is used for passing log informations to redis")
    private String logId;

    @Argument(abbreviation = "po", full = "poweron")
    private Boolean powerOn;

    @Argument(abbreviation = "nw", full = "network")
    private Boolean accessNetwork;

    /**
     * 兼容项
     */
    @Argument(abbreviation = "S", full = "share", hasArgument = true)
    private String share;

    /**
     * 兼容项
     */
    @Argument(abbreviation = "e", full = "vihost")
    private Boolean viHost;

}
