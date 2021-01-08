package com.mlcloud.fusioncloud.bean.args;

import com.mlcloud.cli.anno.Argument;
import com.mlcloud.cli.anno.Arguments;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.defination.mapper.MapTo;
import com.mlcloud.fusioncloud.bean.service.accept.BackupParam;
import lombok.Data;


@Data
@Arguments(action = "backup")
@MapTo(BackupParam.class)
public class BackupArgumentsBean extends ArgumentsBean {

    @Argument(abbreviation = "c", full = "conf", hasArgument = true, required = true, argumentName = "file name", description = "[ARGUMENT]: Platform configuration file")
    private String conf;

    @Argument(abbreviation = "m", full = "moref", hasArgument = true, required = true, argumentName = "id", description = "[ARGUMENT]: UUID of source virtual machine")
    private String instanceId;

    @Argument(abbreviation = "g", full = "generationNum", hasArgument = true, required = true, description = "[ARGUMENT]: Backup generationNum")
    private String generation;

    @Argument(abbreviation = "bo", full = "backup-type", hasArgument = true, argumentName = "type", description = "[ARGUMENT]: Backup object, options: vm/disk")
    private String backupType;

    @Argument(abbreviation = "full", full = "full", description = "[ARGUMENT]: Backup type, options: isFull/incr")
    private Boolean isFull;

    @Argument(abbreviation = "logId", full = "log-id", hasArgument = true, required = true, argumentName = "log id", description = "[ARGUMENT]: This is used for passing log informations to redis")
    private String logId;

    @Argument(abbreviation = "H", full = "localhost", hasArgument = true)
    private String host;

    /**
     * 兼容项
     */
    @Argument(abbreviation = "lsg", full = "last-success-gen", hasArgument = true)
    private String lastSuccessGen;

}
