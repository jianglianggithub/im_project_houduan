package com.jl.common.vo;


import com.jl.common.entity.UserInfo;
import lombok.Data;

@Data
public class LoginSuccessVo {

    private String token;
    private UserInfo userInfo;

}
