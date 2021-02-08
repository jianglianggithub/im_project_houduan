package com.jl.register;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.jl"})
@MapperScan(basePackages = {"com.jl.common.dao"})
public class RegisterApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegisterApplication.class,args);
    }
    @Bean
    public RestTemplate restTemplate(){
        OkHttp3ClientHttpRequestFactory okhttp = new OkHttp3ClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(okhttp);
        // 配置  响应状态码为400  401 的时候 不抛出异常   因为权限不足不算 认证授权失败
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        return restTemplate;
    }

}
