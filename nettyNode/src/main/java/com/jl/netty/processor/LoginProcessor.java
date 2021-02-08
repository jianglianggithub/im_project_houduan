package com.jl.netty.processor;

import com.jl.common.utils.IpUtil;
import com.jl.entity.EventType;
import com.jl.entity.Msg;
import com.jl.netty.manager.RedisUtil;
import com.jl.netty.manager.StateManager;
import com.jl.netty.service.EventService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


@Component
@Slf4j
public class LoginProcessor implements Processor {
    @Autowired
    StateManager stateManager;

    @Autowired
    RedisUtil redisUtil;

    @Value("${const.userInfoCacheRedisKey}")
    String userInfoCacheRedisKey;

    @Autowired
    EventService eventService;




    @Override
    public void handle(String uid, Msg.Model msg, Channel channel) {
        log.info("登录事件触发 uid = {}",uid);
        // 本地缓存 userId - channel 后 并且纪录到redis
        stateManager.addUser(uid,channel);


        // 缓存当前用户上线的node到Redis 便于跨Netty 集群之前 通信
        // 那么在接收 1对1 发送消息的时候 通过接收消息的uid 去查询redis 找到对应nettyNode 的 [ip]
        // 那么 在kafka consumerListener 监听时候 消费者组 直接是uuid 即可。
        // 在发送消息的时候 指定 Heads  key = tags : value = ip
        redisUtil.hset(userInfoCacheRedisKey,uid,stateManager.localUUID);
        //登录成功回执。
        eventService.loginSuccessAck(channel,uid);


    }

    @Override
    public boolean test(int eventType) {
        return eventType == EventType.EventType_4;
    }
}
