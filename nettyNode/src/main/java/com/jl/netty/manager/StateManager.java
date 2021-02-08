package com.jl.netty.manager;


import com.jl.entity.CallMediaPipeline;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StateManager {


    public String localUUID;

    private Map<String, Wrapper> users = new ConcurrentHashMap<>();

    // 用于管理 peer 2 peer 的管道， 这个管道是 kurento 的逻辑实现
    private final ConcurrentHashMap<String, CallMediaPipeline>
            pipelines = new ConcurrentHashMap<>();

    public Wrapper getUserChannelById(String uid) {
        return users.get(uid);
    }

    public boolean checkChannelIsActive(Channel channel) {
        return ((NioSocketChannel)channel).isActive();
    }

    public Channel getChannelIsActive(String uid) {
        Wrapper wrapper = getUserChannelById(uid);
        if (wrapper == null) {
            return null;
        }
        if (!checkChannelIsActive(wrapper.channel)) {
            removeUser(uid);
            wrapper.channel.close();
        }

        return wrapper.channel;
    }

    public void addUserPipeline(String userId,CallMediaPipeline pipeline) {
        pipelines.put(userId,pipeline);
    }

    public void addUser(String uid,Channel channel) {
        log.info("用户上线 uid = {} address {}",uid,channel.remoteAddress());
        Wrapper wrapper = new Wrapper();
        wrapper.channel = channel;
        Wrapper oldWrapper = users.put(uid, wrapper);
        if (oldWrapper != null) {
            log.info("当前用户为重复上线之前通道还有缓存 准备关闭oldChannel");
            NioSocketChannel socketChannel = (NioSocketChannel) oldWrapper.channel;

            // 用于判断 是否是重复登录关闭之前的通道 还是自然的关闭通道
            Attribute<Boolean> reset = socketChannel.attr(AttributeKey.valueOf("reset"));
            reset.set(true);
            socketChannel.close();
        }
    }

    public void removeUser(String uid) {
        log.info("用户下线 uid = {} ",uid);
        Wrapper remove = users.remove(uid);
        if (remove != null) {
            remove.channel.close();
        }

    }

    public CallMediaPipeline removeUserPipeline(String userId) {
        return pipelines.remove(userId);
    }

    public void releasePipeline(String userId) {
        CallMediaPipeline pipeline = this.removeUserPipeline(userId);
        if (pipeline != null) {
            pipeline.release();
        }
    }



    @Data
    public static class Wrapper {
        Channel channel;
        // 是否在通话中。
        boolean connecting;
        // 通话对象
        String connectUid;

        // 发起消息时缓存的 offer
        String sdp;

        // peerConnection
        WebRtcEndpoint webRtcEndpoint;

        final List<IceCandidate> candidateList = new ArrayList<IceCandidate>();



        public  void addIceCandidate(IceCandidate wrapperIceCandidate) {
            if (webRtcEndpoint != null) {
                log.info("addIceCandidate webRtcEndpoint != null");
                webRtcEndpoint.addIceCandidate(wrapperIceCandidate);
            } else {
                log.info("addIceCandidate webRtcEndpoint = null");
                candidateList.add(wrapperIceCandidate);
            }

        }

        public void handleIceCandidate() {
            if (candidateList.size() > 0) {
                log.info("handleIceCandidate candidateList size > 0");
                candidateList.forEach(item -> webRtcEndpoint.addIceCandidate(item));
            }
        }

        public void clear() {
            candidateList.clear();
        }
    }
}
