package com.jl.netty.processor.webrtc;

import com.alibaba.fastjson.JSON;
import com.jl.entity.EventType;
import com.jl.entity.Msg;
import com.jl.entity.WebRtcWrapper;
import com.jl.netty.processor.Processor;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


// webRTC 事件总线处理器
@Slf4j
@Component
public class WebRtcWrapperProcessor implements Processor, ApplicationContextAware {


    Map<Integer, WebRtcProcessor> handles = new HashMap<>();


    @Override
    public void handle(String uid, Msg.Model msg, Channel channel) {
        log.info("webrtc 事件发生 uid  {} ",uid);
        String _msg = msg.getContent().toStringUtf8();
        WebRtcWrapper wrapper = JSON.parseObject(_msg, WebRtcWrapper.class);


        handles.get(wrapper.getEventType()).handle(wrapper,channel,uid);
    }

    @Override
    public boolean test(int eventType) {
        return EventType.EventType_13 == eventType;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {


        ctx
                .getBeansOfType(WebRtcProcessor.class)
                .entrySet()
                .forEach(entry -> {
            WebRtcProcessor value = entry.getValue();
            Integer state = value.getState();
            log.info("handle 添加 state = {} , processor = {}",state,value);
            handles.put(state,value);
        });



    }

}
