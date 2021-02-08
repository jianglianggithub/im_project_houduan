package com.jl.common.vo;


import com.jl.common.entity.UserFirends;
import com.jl.common.entity.UserInfo;
import lombok.Data;

@Data
public class MailListVo {

    private UserFirends userFirends;
    private UserInfo userInfo;
}
