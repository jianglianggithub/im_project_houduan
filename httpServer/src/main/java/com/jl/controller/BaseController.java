package com.jl.controller;

import com.jl.register.config.UserExt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {

    public String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserExt)authentication.getPrincipal()).getUserId();
    }
}
