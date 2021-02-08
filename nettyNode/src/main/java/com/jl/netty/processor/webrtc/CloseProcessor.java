package com.jl.netty.processor.webrtc;

import com.jl.entity.WebRtcWrapper;
import com.jl.netty.manager.StateManager;
import com.jl.netty.service.EventService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CloseProcessor implements WebRtcProcessor  {

    @Autowired
    StateManager stateManager;

    @Autowired
    EventService eventService;

    @Override
    public void handle(WebRtcWrapper wrapper, Channel channel, String uid) {
        WebRtcWrapper.Action action = wrapper.getAction();

        String from = action.getFrom();
        String to = action.getTo();

        stateManager.releasePipeline(from);
        stateManager.releasePipeline(to);



        StateManager.Wrapper fromUser = stateManager.getUserChannelById(from);
        StateManager.Wrapper toUser = stateManager.getUserChannelById(to);


        // close 之后 状态也要恢复成未拨打状态



        if (fromUser != null) {
            fromUser.setWebRtcEndpoint(null);
            fromUser.getCandidateList().clear();

            fromUser.setConnecting(false);
            fromUser.setConnectUid(null);
        }

        if (toUser != null) {
            toUser.setWebRtcEndpoint(null);
            toUser.getCandidateList().clear();

            toUser.setConnecting(false);
            toUser.setConnectUid(null);
        }
        /**
         *  释放资源后 当一方向另外一端 发送close 事件 让对方 感知到 并且关闭 通话
         */

        // 发起聊天的是from 那么 from 否则 向 to 发送 close 事件
        eventService.closeMedia(uid.equals(from) ? to : from);


    }

    @Override
    public Integer getState() {
        return WebRtcWrapper.type_5;
    }
}
