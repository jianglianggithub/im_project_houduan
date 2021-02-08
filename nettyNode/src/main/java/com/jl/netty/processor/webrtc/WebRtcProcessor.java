package com.jl.netty.processor.webrtc;

import com.jl.entity.WebRtcWrapper;
import io.netty.channel.Channel;

public interface WebRtcProcessor {

    void handle(WebRtcWrapper wrapper, Channel channel,String uid);

    Integer getState();
}
