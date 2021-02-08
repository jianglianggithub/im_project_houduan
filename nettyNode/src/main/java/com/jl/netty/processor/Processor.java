package com.jl.netty.processor;

import com.jl.entity.Msg;
import io.netty.channel.Channel;

public interface Processor {


    void handle(String uid, Msg.Model msg, Channel channel);

    /**
     *
     * @param eventType 事件类型
     * @return  是否匹配该处理器
     */
    boolean test(int eventType);
}
