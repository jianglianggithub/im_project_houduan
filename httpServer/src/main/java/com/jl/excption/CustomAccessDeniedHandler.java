package com.jl.excption;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jl.common.api.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ClassName: CustomAccessDeniedHandler
 *
 * @author L.G
 * @Description 自定义权限异常
 * @email lg10000@126.com
 * @date 2018年8月2日 下午5:00:32
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(200);
        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(CommonResult.unauthorized("无权限")));
    }

}
