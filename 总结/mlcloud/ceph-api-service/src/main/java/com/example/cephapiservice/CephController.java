package com.example.cephapiservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ：hf
 * @date ：Created in 2020/12/12 9:04 下午
 * @description：
 * @modified By：
 * @version: $
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class CephController {

    @Resource
    private CephApiServiceApplication api;

    @RequestMapping("/hello")
    public void hello(){
        log.info("hello========");
    }
    @RequestMapping("/conn")
    public void conn(){
        log.info("start for connect=======");
        RbdDao.connectCeph();
        log.info("end for connect==========");
    }
    @RequestMapping("/images")
    public void listImages(){
        log.info("start for listImages=======");
        RbdDao.imageList();
        log.info("end for connect==========");
    }
}
