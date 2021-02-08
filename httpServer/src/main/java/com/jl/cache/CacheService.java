package com.jl.cache;


import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@Slf4j
public class CacheService implements EventListener {


    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private List<Instance> service;


    public void initService(List<Instance> nettyServers) {
        try {
            readWriteLock.writeLock().lockInterruptibly();
            try {
                service = nettyServers;
            }finally {
                readWriteLock.writeLock().unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addService(Instance instance) {
        try {
            readWriteLock.writeLock().lockInterruptibly();
            try {
                service.add(instance);
            }finally {
                readWriteLock.writeLock().unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(Event event) {

        if (event instanceof NamingEvent) {
            List<Instance> instances = ((NamingEvent) event).getInstances();
            initService(instances);
            log.info("[重新刷新服务列表] event new Instances = {}",instances);
        }
    }

    public String getService() {
        try {
            readWriteLock.readLock().lockInterruptibly();
            try {
                Random r = new Random(1);
                if (service.size() < 1) {
                    return "";
                }
                int idx = r.nextInt(service.size());
                Instance instance = service.get(idx);
                String ip = instance.getIp();
                return ip + ":2222";
            }finally {
                readWriteLock.readLock().unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
