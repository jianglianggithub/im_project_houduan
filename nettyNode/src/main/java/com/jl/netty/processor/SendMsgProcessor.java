package com.jl.netty.processor;

import com.jl.entity.EventType;
import com.jl.entity.Msg;
import com.jl.netty.service.EventService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendMsgProcessor implements Processor {

    @Autowired
    EventService eventService;

    @Override
    public void handle(String uid, Msg.Model msg, Channel channel) {
        String receiver = msg.getReceiver();
        String msgBody = msg.getContent().toStringUtf8();
        long sendTime = msg.getSendTime();
        log.info("[发送消息事件] uid = {} receiverId = {} msg = {} sendTime = {}",uid,receiver,msgBody,sendTime);
        eventService.preSendMsg(uid,msg,channel);
    }

    @Override
    public boolean test(int eventType) {
        return eventType == EventType.EventType_2;
    }
}
