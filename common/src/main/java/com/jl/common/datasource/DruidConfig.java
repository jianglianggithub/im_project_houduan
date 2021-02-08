package com.jl.common.datasource;


import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DruidConfig {
    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "druid")
    public DataSource dataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();

    }
//
//    @Bean(name = "wallFilter")
//    @DependsOn("wallConfig")
//    public WallFilter wallFilter(){
//        WallFilter wallFilter = new WallFilter();
//        wallFilter.setConfig(wallConfig());
//        return wallFilter;
//    }
//
//    @Bean(name = "wallConfig")
//    public WallConfig wallConfig(){
//        WallConfig wallConfig = new WallConfig();
//        wallConfig.setMultiStatementAllow(true);//允许一次执行多条语句
//        wallConfig.setNoneBaseStatementAllow(true);//允许一次执行多条语句
//        return wallConfig;
//    }

}
