package com.mlcloud.fusioncloud;

import com.google.gson.Gson;
import com.mlcloud.cli.CommandDispacher;
import com.mlcloud.cli.bean.ExecuteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author ：hf
 * @date ：Created in 2020/11/24 11:29 上午
 * @description：openstack demo 的入口 main函数
 * @modified By：
 * @version: $
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String COMMAND_NAME = "fusioncloudMgr";
    public static void main(String[] args) {
        CommandDispacher dispacher = new CommandDispacher(Main.class);
        if (args == null || args.length == 0) {
            dispacher.printHelpList();
        }
        else {
            logCommand(args);
            ExecuteResult res = dispacher.dispatch(args);
            logResult(res);
        }

    }

    /**
     * @description: 记录操作命令到日志
     * @param args
     * @return void
     */
    private static void logCommand(String[] args){
        StringBuilder sb = new StringBuilder();
        sb.append(COMMAND_NAME);
        sb.append(" ");
        Arrays.stream(args).forEach(arg ->{
            sb.append(arg);
            sb.append(" ");
        });

        logger.info("cmd:{}\n",sb);
    }
    /**
     * @description:记录执行结果到日志
     * @param result
     * @return void
     */
    private static void logResult(ExecuteResult result){
        if(result == null){
            return;
        }

        Gson gson = new Gson();
        String outMsg = gson.toJson(result);
        if(result.isSuccess()){
            logger.info(outMsg);
        }else{
            logger.error(outMsg);
        }
    }


}
