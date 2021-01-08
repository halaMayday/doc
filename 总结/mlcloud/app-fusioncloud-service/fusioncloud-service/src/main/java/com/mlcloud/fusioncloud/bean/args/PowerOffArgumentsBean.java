package com.mlcloud.fusioncloud.bean.args;

import com.mlcloud.cli.anno.Argument;
import com.mlcloud.cli.anno.Arguments;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.defination.mapper.MapTo;
import com.mlcloud.fusioncloud.bean.service.accept.PoweroffParam;
import lombok.Data;

/**
 * @author ：hf
 * @date ：Created in 2020/12/19 6:08 下午
 */
@Data
@Arguments(action = "power-off-vm")
@MapTo(PoweroffParam.class)
public class PowerOffArgumentsBean extends ArgumentsBean {

    @Argument(abbreviation = "c", full = "conf", hasArgument = true, required = true, argumentName = "file name", description = "[ARGUMENT]: Platform configuration file")
    private String conf;

    @Argument(abbreviation = "m", full = "moref", hasArgument = true, required = true, argumentName = "id", description = "[ARGUMENT]: UUID of virtual machine")
    private String instanceId;
}
