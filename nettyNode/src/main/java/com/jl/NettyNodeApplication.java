package com.jl;


import com.jl.netty.manager.StateManager;
import org.kurento.client.KurentoClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.StreamListenerAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.jl.common.dao"})
@EnableFeignClients
public class NettyNodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(NettyNodeApplication.class,args);
    }

    @Bean
    public KurentoClient kurentoClient() {
        return KurentoClient.create("ws://35.221.188.213:8888/kurento");
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
    private static String templateCondition = "headers['tag']=='%s'"; // 每个netty 节点 监听的tag = 本机ip

    /**
     *  自定义注解 condition 动态更改
     * @param stateManager
     * @return
     */
    @Bean
    public StreamListenerAnnotationBeanPostProcessor streamListenerAnnotationBeanPostProcessor(StateManager stateManager) {
        return new StreamListenerAnnotationBeanPostProcessor() {
            @Override
            protected StreamListener postProcessAnnotation(StreamListener streamListener, Method annotatedMethod) {

                try {

                    InvocationHandler h = Proxy.getInvocationHandler(streamListener);
                    Field hField = h.getClass().getDeclaredField("valueCache");

                    hField.setAccessible(true);

                    Map memberValues = (Map) hField.get(h);

                    memberValues.put("condition", String.format(templateCondition,stateManager.localUUID));

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }


                return super.postProcessAnnotation(streamListener, annotatedMethod);
            }
        };
    }

}
