package com.jl.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.jl.common.api.CommonResult;

import java.util.List;

public interface NacosClientSerivce {

    List<Instance> getService(String name);

    void cacheServiceList();

    void subscribeService() throws NacosException;

    CommonResult<?> getWebSocketServer();
}
