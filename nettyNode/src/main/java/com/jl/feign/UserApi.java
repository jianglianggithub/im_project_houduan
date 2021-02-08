package com.jl.feign;


import com.jl.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(serviceId = "http://httpServer")
public interface UserApi {


    @RequestMapping("/user/getUserInfoByToken")
    CommonResult<String> getUserInfoByToken(@RequestParam("token")String token);
}
