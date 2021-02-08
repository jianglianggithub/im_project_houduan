package com.jl.netty.processor.webrtc;

import com.alibaba.fastjson.JSON;
import com.jl.entity.WebRtcWrapper;
import com.jl.netty.manager.StateManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@Component
public class ICECondidateProcessor implements WebRtcProcessor  {

    @Autowired
    StateManager stateManager;


    @Override
    public void handle(WebRtcWrapper wrapper, Channel channel, String uid) {
        log.info("接收到icecandidate uid = {} ,wrapper = {}",uid,wrapper);
        StateManager.Wrapper iceUser = stateManager.getUserChannelById(uid);
        if (iceUser == null) {
            log.info("用户已下线 或者未上线 无法添加iceCandidate  userId = {}",uid);
            return;
        }
        String iceCondidate = wrapper.getIceCondidate();
        Map iceCandidate = JSON.parseObject(iceCondidate, Map.class);
        String candidate = (String) iceCandidate.get("candidate");
        String sdpMid = (String) iceCandidate.get("sdpMid");
        Integer sdpMLineIndex = (Integer) iceCandidate.get("sdpMLineIndex");
        IceCandidate wrapperIceCandidate = new IceCandidate(candidate, sdpMid, sdpMLineIndex);

        iceUser.addIceCandidate(wrapperIceCandidate);
    }

    @Override
    public Integer getState() {
        return WebRtcWrapper.type_4;
    }
}
