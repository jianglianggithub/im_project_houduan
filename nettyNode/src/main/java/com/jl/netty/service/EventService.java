package com.jl.netty.service;

import com.jl.common.pojo.Model;
import com.jl.entity.Msg;
import com.jl.entity.WebRtcWrapper;
import io.netty.channel.Channel;

public interface EventService {

    void loginSuccessAck(Channel channel,String uid);

    void loginFailAck(Channel channel, String uid);

    void sendMsg(Model model);
    void sendMsg(String userId, WebRtcWrapper webRtcWrapper);

    void preSendMsg(String uid, Msg.Model msg,Channel channel);

    void removeRedisCache(String userId);

    void unReadMsg(String uid, Channel channel);

    void ackMeessage(String[] ids);

    void contactsRecord(String uid, Channel channel);

    void recoveryFriendRecord(String uid, String receiver, Channel channel);

    void CallFailed(Channel channel);

    void call(Channel channel,String uid,String to);

    void friendsGet(String uid, Channel channel);

    void closeMedia(String s);
}
