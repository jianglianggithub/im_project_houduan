package com.jl.netty.processor;

import com.jl.entity.EventType;
import com.jl.entity.Msg;
import com.jl.netty.service.EventService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 获取好友列表
@Component
@Slf4j
public class FriendsGetProcessor implements Processor  {
    @Autowired
    EventService eventService;

    @Override
    public void handle(String uid, Msg.Model msg, Channel channel) {
        log.info("获取好友列表 id = {}",uid);
        eventService.friendsGet(uid,channel);
    }

    @Override
    public boolean test(int eventType) {
        return EventType.EventType_14 == eventType;
    }
}
