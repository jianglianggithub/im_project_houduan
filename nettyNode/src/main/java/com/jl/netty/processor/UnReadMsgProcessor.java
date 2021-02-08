package com.jl.netty.processor;

import com.jl.entity.EventType;
import com.jl.entity.Msg;
import com.jl.netty.service.EventService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UnReadMsgProcessor implements Processor {


    @Autowired
    EventService eventService;


    @Override
    public void handle(String uid, Msg.Model msg, Channel channel) {
        log.info("用户拉取未读消息事件触发 uid = {}",uid);
        eventService.unReadMsg(uid,channel);
    }

    @Override
    public boolean test(int eventType) {
        return eventType == EventType.EventType_7;
    }
}
