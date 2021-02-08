package com.jl.excption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jl.common.api.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ClassName: CustomAuthExceptionEntryPoint
 *
 * @author L.G
 * @Description 自定义认证异常信息
 * @email lg10000@126.com
 * @date 2018年8月2日 下午4:58:42
 */
@Component
public class CustomAuthExceptionEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("static-access")
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
//        Throwable cause = authException.getCause();
//        if (cause instanceof InvalidTokenException) {
//            result = Result.custom(ResultConstants.TOKEN_INVALID_CODE, "无效的令牌", null);
//        } else {
//            result = Result.fail("访问此资源需要完全的身份验证");
//        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(200);
        PrintWriter printWriter = response.getWriter();
        boolean tmep=authException.getCause() instanceof InvalidTokenException;
        printWriter.append(
                objectMapper.writeValueAsString(CommonResult.unauthorized(tmep ? "token无效" : "没有认证"))
                );
    }

}