package com.mlcloud.fusioncloud.controller;


import com.mlcloud.cli.CommandDispacher;
import com.mlcloud.cli.anno.Action;
import com.mlcloud.cli.bean.ArgumentsBean;
import com.mlcloud.cli.bean.ExecuteResult;
import com.mlcloud.cli.controller.CommandController;

/**
 * @author ：hf
 * @date ：Created in 2020/11/24 1:50 下午
 * @description： get help list
 * @modified By：
 * @version: $
 */
@Action(name = "help",abbreviation = "h",full = "help",description = "[ACTION]: get help list")
public class HelpController  implements CommandController {

    @Override
    public <T extends ArgumentsBean> ExecuteResult execute(T argsBean) {
        new CommandDispacher(HelpController.class).printHelpList();
        return null;
    }


}
