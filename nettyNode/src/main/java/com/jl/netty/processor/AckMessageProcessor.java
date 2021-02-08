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
public class AckMessageProcessor implements Processor {

    @Autowired
    EventService eventService;

    @Override
    public void handle(String uid, Msg.Model msg, Channel channel) {
        String msgBody = msg.getContent().toStringUtf8();
        log.info("uid = {} ack事件 wrapperBody = {}",uid,msgBody);

        String[] ids = msgBody.split(",");
        eventService.ackMeessage(ids);
    }

    @Override
    public boolean test(int eventType) {
        return EventType.EventType_8 == eventType;
    }
}
