package com.jl.netty.kafka;


import com.alibaba.fastjson.JSON;
import com.jl.common.pojo.Model;
import com.jl.netty.manager.StateManager;
import com.jl.netty.processor.LoginProcessor;
import com.jl.netty.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

@Service
@Slf4j
@EnableBinding(MySource.class)
public class MessageListener  {


    @Autowired
    StateManager stateManager;


    /**
     *  该代码块 用于 将condition 条件启动时 动态加载 当前NettyNode 启动监听对应topic下的消息时候
     *  只监听 发送给当前节点的消息。
     */



    @Autowired
    EventService eventService;


    @StreamListener(value = MySource.myInput)
    public void receive(String messageBody) {
        log.info("收到了消息：messageBody ={}", messageBody);
        Model model = JSON.parseObject(messageBody, Model.class);
        try {
            eventService.sendMsg(model);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("消息处理失败");
        }

    }



}
