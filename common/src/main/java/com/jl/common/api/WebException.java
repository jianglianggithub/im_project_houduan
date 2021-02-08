package com.jl.common.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class WebException {

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public CommonResult<?> customException(CustomException customException){
        log.error("catch exception:  >>>>>"+customException.getCommonResult().getMessage());
        return customException.getCommonResult();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResult<?> customException(Exception exception){
        log.error("catch exception:  >>>>>"+exception.getMessage());
        exception.printStackTrace();
        return CommonResult.failed(exception.getMessage());
    }

//    @ExceptionHandler(AccessDeniedException.class)
//    @ResponseBody
//    public CommonResult<?> customException(AccessDeniedException exception){
//        log.error("catch exception:  >>>>>"+exception.getMessage());
//        return CommonResult.failed("没有权限访问");
//    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public CommonResult<?> customException(HttpMediaTypeNotSupportedException exception){
        log.error("catch exception:  >>>>>"+exception.getMessage());
        return CommonResult.failed(exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public CommonResult<?> customException(HttpRequestMethodNotSupportedException exception){
        log.error("catch exception:  >>>>>"+exception.getMessage());
        return CommonResult.failed(exception.getMessage());
    }
}
