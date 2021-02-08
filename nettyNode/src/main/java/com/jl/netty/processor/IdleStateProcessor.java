package com.jl.netty.processor;


import com.jl.entity.EventType;
import com.jl.entity.Msg;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IdleStateProcessor implements Processor {
    @Override
    public void handle(String uid, Msg.Model msg, Channel channel) {
      //  log.info("[客户端心跳事件] uid {} ",uid);
    }

    @Override
    public boolean test(int eventType) {
        return eventType == EventType.EventType_1;
    }
}
