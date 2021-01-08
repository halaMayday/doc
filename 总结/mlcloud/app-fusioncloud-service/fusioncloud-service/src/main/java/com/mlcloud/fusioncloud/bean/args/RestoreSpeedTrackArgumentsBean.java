package com.mlcloud.fusioncloud.bean.args;

import com.mlcloud.cli.anno.Argument;
import com.mlcloud.cli.anno.Arguments;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.defination.mapper.MapTo;
import com.mlcloud.fusioncloud.bean.service.accept.RestoreSpeedTrackParam;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 6:11 下午
 */
@Data
@Arguments(action = "restore-speed-track")
@MapTo(RestoreSpeedTrackParam.class)
public class RestoreSpeedTrackArgumentsBean extends ArgumentsBean {

    @Argument(abbreviation = "m", full = "moref", required = true, hasArgument = true, description = "[ARGUMENT]: Id of instance to be restored")
    private String instanceId;

    @Argument(abbreviation = "g", full = "generationNum", required = true, hasArgument = true, description = "[ARGUMENT]: Restore generationNum")
    private String generation;

    @Argument(abbreviation = "logId", full = "log-id", hasArgument = true, required = true, argumentName = "log id", description = "[ARGUMENT]: This is used for passing log informations to redis")
    private String logId;

    @Argument(abbreviation = "c", full = "conf", hasArgument = true, required = true, argumentName = "file name", description = "[ARGUMENT]: Platform configuration file")
    private String conf;

}
