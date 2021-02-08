package com.jl.netty.processor.webrtc;

import com.alibaba.fastjson.JSON;
import com.jl.entity.CallMediaPipeline;
import com.jl.entity.WebRtcWrapper;
import com.jl.netty.manager.StateManager;
import com.jl.netty.service.EventService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.KurentoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class CallProcessor implements WebRtcProcessor {

    @Autowired
    StateManager stateManager;

    @Autowired
    EventService eventService;

    @Autowired
    KurentoClient kurentoClient;




    @Override
    public void handle(WebRtcWrapper wrapper, Channel channel, String uid) {
        WebRtcWrapper.Action action = wrapper.getAction();
        if (action == null) {
            log.error("如果 是拨打电话 action obj 不能为null");
            return;
        }

        Integer eventType = action.getEventType();

        log.info("收到Call 消息 wrapper = {}",wrapper);

        // 1= 发起视频通话
        if (eventType == WebRtcWrapper.Action.type_1) {
            String to = action.getTo();
            StateManager.Wrapper fromUser = stateManager.getUserChannelById(uid);
            StateManager.Wrapper toUser = stateManager.getUserChannelById(to);


            // 拨打方 接收方 为通话状态时 不允许打电话
            if (toUser == null || toUser.isConnecting() || fromUser.isConnecting()) {
                eventService.CallFailed(channel);
            } else {
                /*
                   这里会有一个情况 A call b  的同时 B 也在 call A
                   通过锁的重入来解决 同时拨打问题

                   双 synchronized 会产生死锁。不能这么玩。。
                   生产的话 用redisson 吧
                */

                        /* 如果一旦发起拨打 发起人 接收人 全部设置为 拨打状态无法再次打电话 */

                        // 缓存sdp offer 等待同意时 将offer 传达给 ksm
                        if (!toUser.isConnecting() && !fromUser.isConnecting()) {
                            fromUser.setSdp(wrapper.getSdp());
                            fromUser.setConnecting(true);
                            fromUser.setConnectUid(to);

                            toUser.setConnecting(true);
                            toUser.setConnectUid(uid);
                            eventService.call(toUser.getChannel(), uid,to);
                        } else {
                            eventService.CallFailed(channel);
                        }


            }

        }
        // 2= 回应是否接听这个会话
        else if (eventType == WebRtcWrapper.Action.type_2) {
            Boolean allow = action.getAllow();
            StateManager.Wrapper fromUser = stateManager.getUserChannelById(action.getFrom());
            StateManager.Wrapper toUser = stateManager.getUserChannelById(action.getTo());
            // 同意接听会话
            if (allow) {

                boolean ok = this.callAllow(
                        fromUser,
                        toUser,
                        action.getFrom(),
                        action.getTo(),
                        wrapper.getSdp()
                );
                if (!ok) {
                    callFailed(action);
                }
            } else {
                callFailed(action);
            }

        }

    }

    private void callFailed(WebRtcWrapper.Action action) {
        // 不接听 像拨打方 回复 接收人不接听此次会话
        Channel fromChannel = stateManager.getChannelIsActive(action.getFrom());
        if (fromChannel != null) {

            // 恢复 双端未连接 tag
            StateManager.Wrapper fromUser = stateManager.getUserChannelById(action.getFrom());
            StateManager.Wrapper toUser = stateManager.getUserChannelById(action.getTo());
            if (fromUser != null) {
                fromUser.setConnecting(false);
                fromUser.setConnectUid(null);
            }
            if (toUser != null) {
                toUser.setConnecting(false);
                toUser.setConnectUid(null);
            }

            eventService.CallFailed(fromChannel);
        } else {
            log.error("拨打方不在线 or 创建 peer to peer 管道异常  拨打失败 fromId = {}",action.getFrom());
        }


    }
    private boolean callAllow(
            StateManager.Wrapper from
            , StateManager.Wrapper to
            , String fromUser
            , String toUser
            , String toSdpOffer
    ) {
        CallMediaPipeline pipeline = null;
        try {

            // 创建1对1 的 媒体管道 并且 创建连接2个端点
            pipeline = new CallMediaPipeline(kurentoClient);
            stateManager.addUserPipeline(fromUser, pipeline);
            stateManager.addUserPipeline(toUser, pipeline);


            from.setWebRtcEndpoint(pipeline.getFromWebRtcEp());
            pipeline.getFromWebRtcEp().addIceCandidateFoundListener(getListener(fromUser,null));

            to.setWebRtcEndpoint(pipeline.getToWebRtcEp());
            pipeline.getToWebRtcEp().addIceCandidateFoundListener(getListener(null,toUser));


            String toSdpAnswer = pipeline.generateSdpAnswerForCallee(toSdpOffer);
            sendAnswerToUser(toUser,toSdpAnswer);
            pipeline.getToWebRtcEp().gatherCandidates();


            String fromSdpAnswer = pipeline.generateSdpAnswerForCaller(from.getSdp());
            sendAnswerToUser(fromUser,fromSdpAnswer);
            pipeline.getFromWebRtcEp().gatherCandidates();


            //  查询是否已经有iceCandidate 准备好了 因为在 被拨打一方没有接收到 offer 回复answer 之前可能ice就已经准备就绪了都
            from.handleIceCandidate();
            to.handleIceCandidate();

        }catch (Exception e) {
            e.printStackTrace();
            log.error("管道双向连接异常 callAllow");
            if (pipeline != null) {
                pipeline.release();
            }
            stateManager.removeUserPipeline(fromUser);
            stateManager.removeUserPipeline(toUser);

            from.clear();
            to.clear();
            return false;
        }


        return true;

    }

    public void sendAnswerToUser(String userId,String answer) {
        WebRtcWrapper init = WebRtcWrapper.getInit();
        init.setEventType(WebRtcWrapper.type_2);
        init.setAnswer(answer);
        eventService.sendMsg(userId,init);
    }

    public EventListener<IceCandidateFoundEvent> getListener(String from,String to) {

        return event -> {
            String tag = from != null ? from : to;
            WebRtcWrapper init = WebRtcWrapper.getInit();
            init.setEventType(WebRtcWrapper.type_4);
            init.setIceCondidate(JSON.toJSONString(event.getCandidate()));
            eventService.sendMsg(tag,init);
        };
    }

    @Override
    public Integer getState() {
        return WebRtcWrapper.type_3;
    }
}
