package com.jl;


import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.jl.common.dao"})
public class HttpServerApplication {
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    String serverAddr;


    public static void main(String[] args) {
        SpringApplication.run(HttpServerApplication.class,args);
    }

    @Bean
    public NamingService namingService() throws NacosException {
        return NamingFactory.createNamingService(serverAddr);
    }
}
