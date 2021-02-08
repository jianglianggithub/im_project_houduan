package com.jl.controller;



import com.jl.common.api.CommonResult;
import com.jl.service.NacosClientSerivce;
 import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/nacos")
public class NacosController {

    @Autowired
    NacosClientSerivce nacosClientSerivce;

    @RequestMapping("/getWebSocketServer")
    public CommonResult<?> getWebSocketServer() {
        return nacosClientSerivce.getWebSocketServer();
    }
}
