package com.jl.common.api;


import com.baomidou.mybatisplus.extension.api.IErrorCode;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CustomException extends RuntimeException{

    private CommonResult<?> commonResult;

    public CustomException(String msg){
        this.commonResult= CommonResult.failed(msg);
    }

    public CustomException(IErrorCode resultCode){
        this.commonResult=CommonResult.failed(resultCode);
    }

    public CommonResult<?> getCommonResult() {
        return commonResult;
    }
}
