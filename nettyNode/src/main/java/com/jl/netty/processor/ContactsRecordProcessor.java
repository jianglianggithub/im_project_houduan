package com.jl.netty.processor;


import com.jl.entity.EventType;
import com.jl.entity.Msg;
import com.jl.netty.service.EventService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 获取联系人页面纪录
@Component
@Slf4j
public class ContactsRecordProcessor implements Processor  {


    @Autowired
    EventService eventService;

    @Override
    public void handle(String uid, Msg.Model msg, Channel channel) {
       log.info("uid = {} 拉取联系人 纪录",uid);
        eventService.contactsRecord(uid, channel);
    }

    @Override
    public boolean test(int eventType) {
        return EventType.EventType_9 == eventType;

    }
}
