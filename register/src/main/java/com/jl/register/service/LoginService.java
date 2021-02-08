package com.jl.register.service;


import com.jl.common.api.CommonResult;
import com.jl.common.api.CustomException;
import com.jl.common.entity.UserInfo;
import com.jl.common.service.UserInfoService;
import com.jl.common.vo.LoginSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class LoginService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${server.port}")
    private String port;

    @Autowired
    UserInfoService userInfoService;

    public CommonResult<?> login(String username, String password) {
        //配置请求头
        LinkedMultiValueMap<String, String> heard = new LinkedMultiValueMap<>();
        heard.add("Authorization",getHttpBasic("users","123456"));

        // 请求体
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);
        body.add("redirect_uri","http://localhost");

        //发起请求
        HttpEntity<LinkedMultiValueMap> requestParam = new HttpEntity<>(body,heard);
        //会回调UserDeatilService 中的方法
        ResponseEntity<Map> respones = restTemplate.exchange("http://127.0.0.1:17770/register/oauth/token", HttpMethod.POST, requestParam, Map.class,port);
        Map responesParam = respones.getBody();

        //获取响应的 token
        String access_token = (String) responesParam.get("access_token");
        if (StringUtils.isEmpty(access_token)) {
            throw new CustomException("用户名或密码错误");
        }

        UserInfo user = userInfoService.getUserByUserName(username);

        LoginSuccessVo vo = new LoginSuccessVo();
        vo.setUserInfo(user);
        vo.setToken(access_token);

        return CommonResult.success(vo,"");

    }

    private String getHttpBasic(String clientId,String clientSecret){
        String string = clientId+":"+clientSecret;
        //将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }

}
