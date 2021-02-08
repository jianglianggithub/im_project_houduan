package com.jl.entity;


import com.jl.netty.processor.webrtc.WebRtcProcessor;
import lombok.Data;

@Data
public class WebRtcWrapper {

    // offer
    public static final int type_1 = 1;
    // answer
    public static final int type_2 = 2;
    // 拨打电话 ，允许接听，拒绝接听，不在线
    public static final int type_3 = 3;
    // iceCondidate
    public static final int type_4 = 4;
    //close 这个close 可以是 发起方 也可以是接收方 发起的close
    public static final int type_5 = 5;

    Integer eventType;

    Action action;

    /* 该sdp 可以是 发起通话时 发起人的sdp  也可以是 接收这次会话 后 接收人返回的sdp */
    String sdp;
    String answer;
    String iceCondidate;

    public static WebRtcWrapper getInit() {
        WebRtcWrapper webRtcWrapper = new WebRtcWrapper();
        webRtcWrapper.setAction(new Action());
        return webRtcWrapper;
    }
    @Data
    public static class Action {

        // 1= 客户端 发起视频通话
        public static final int type_1 = 1;
        // 2= 对于服务端回应接收人是否接听这个会话   对于客户端是发送自己接听或者不接听这个会话
        public static final int type_2 = 2;
        // 3 = 发送给客户端拒绝接听或者对方不在线
        public static final int type_3 = 3;


        Integer eventType;
        // 是否同意
        Boolean allow;
        // 拨打人
        String from;
        // 接收人
        String to;
        // msg
        String msg;
    }

}
