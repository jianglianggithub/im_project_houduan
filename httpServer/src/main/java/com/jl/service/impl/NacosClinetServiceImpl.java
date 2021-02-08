package com.jl.service.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.jl.common.api.CommonResult;
import com.jl.cache.CacheService;
import com.jl.service.NacosClientSerivce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NacosClinetServiceImpl implements NacosClientSerivce, ApplicationListener<ContextRefreshedEvent> {

   @Autowired
   NamingService namingService;

   @Autowired
   CacheService cacheService;

    @Override
    public List<Instance> getService(String serviceName) {
        try {
            return namingService.getAllInstances(serviceName);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 启动缓存服务列表到本地cache
     */
    @Override
    public void cacheServiceList() {
        List<Instance> nettyServers = getService("NettyServer");
        if (nettyServers != null) {
            cacheService.initService(nettyServers);
            log.info("初始化服务列表 {}",nettyServers);
        }
    }

    @Override
    public void subscribeService() {
        try {
            namingService.subscribe("NettyServer", cacheService);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommonResult<?> getWebSocketServer() {
        String address = cacheService.getService();
        return CommonResult.success(address,"");
    }

    // 用于启动监听 nacos 服务列表改变情况 刷新本地缓存



    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        this.cacheServiceList();
        this.subscribeService();
    }
}
