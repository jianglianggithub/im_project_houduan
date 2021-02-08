package com.jl.register.controller;


import com.jl.common.api.CommonResult;
import com.jl.register.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/login")
public class LoginController {




    @Autowired
    LoginService loginService;

    @RequestMapping("/login")
    public CommonResult<?> login(String username,String password) {
        return loginService.login(username,password);
    }

}
