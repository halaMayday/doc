package com.mlcloud.fusioncloud.bean.args;

import com.mlcloud.cli.anno.Argument;
import com.mlcloud.cli.anno.Arguments;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.defination.mapper.MapTo;
import com.mlcloud.fusioncloud.bean.service.accept.BackupSpeedTrackParam;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 6:06 下午
 */
@Data
@Arguments(action = "backup-speed-track")
@MapTo(BackupSpeedTrackParam.class)
public class BackupSpeedTrackArgumentsBean extends ArgumentsBean {

    @Argument(abbreviation = "m", full = "moref", hasArgument = true, required = true, argumentName = "id", description = "[ARGUMENT]: UUID of source virtual machine")
    private String instanceId;

    @Argument(abbreviation = "c", full = "conf", hasArgument = true, required = true, argumentName = "file name", description = "[ARGUMENT]: Platform configuration file")
    private String conf;

    @Argument(abbreviation = "O", full = "starttime", hasArgument = true)
    private String startTime;
}
