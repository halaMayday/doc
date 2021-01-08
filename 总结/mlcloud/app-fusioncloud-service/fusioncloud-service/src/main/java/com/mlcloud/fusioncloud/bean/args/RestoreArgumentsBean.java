package com.mlcloud.fusioncloud.bean.args;

import com.mlcloud.cli.anno.Argument;
import com.mlcloud.cli.anno.Arguments;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.defination.mapper.MapTo;
import com.mlcloud.fusioncloud.bean.service.accept.RestoreParam;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/14 10:43 上午
 * @description：
 * @modified By：
 * @version: $
 */
@Data
@Arguments(action = "restore")
@MapTo(RestoreParam.class)
public class RestoreArgumentsBean extends ArgumentsBean {

    @Argument(abbreviation = "m", full = "moref", required = true, hasArgument = true, description = "[ARGUMENT]: Id of instance to be restored")
    private String instanceId;

    @Argument(abbreviation = "g", full = "generationNum", required = true, hasArgument = true, description = "[ARGUMENT]: Restore generationNum")
    private String generation;

    @Argument(abbreviation = "logId", full = "log-id", hasArgument = true, required = true, argumentName = "log id", description = "[ARGUMENT]: This is used for passing log informations to redis")
    private String logId;

    @Argument(abbreviation = "c", full = "conf", hasArgument = true, required = true, argumentName = "file name", description = "[ARGUMENT]: Platform configuration file")
    private String conf;

    @Argument(abbreviation = "H", full = "localhost", hasArgument = true, required = true)
    private String host;

    /**
     * 兼容项
     */
    @Argument(abbreviation = "e", full = "vihost")
    private Boolean viHost;

    /**
     * 兼容项
     */
    @Argument(abbreviation = "V", full = "vmname", hasArgument = true)
    private String newVm;

    @Argument(abbreviation = "po", full = "poweron")
    private Boolean powerOn;

    /**
     * 兼容项
     */
    @Argument(abbreviation = "S", full = "share", hasArgument = true)
    private String share;

}

