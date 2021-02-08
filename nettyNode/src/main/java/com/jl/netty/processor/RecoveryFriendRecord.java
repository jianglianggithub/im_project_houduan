package com.jl.netty.processor;


import com.jl.entity.EventType;
import com.jl.entity.Msg;
import com.jl.netty.manager.StateManager;
import com.jl.netty.service.EventService;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 恢复指定好友的 消息

@Component
public class RecoveryFriendRecord implements Processor {
    @Autowired
    EventService eventService;

    @Autowired
    StateManager stateManager;

    @Override
    public void handle(String uid, Msg.Model msg, Channel channel) {
        String receiver = msg.getReceiver();
        eventService.recoveryFriendRecord(uid,receiver,channel);
    }

    @Override
    public boolean test(int eventType) {
        return eventType == EventType.EventType_10;
    }
}
