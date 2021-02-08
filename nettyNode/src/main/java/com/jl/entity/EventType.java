package com.jl.entity;

public class EventType {

    // 心跳事件
    public static final Integer EventType_1 = 1;
    // 发送 1 对 1 消息
    public static final Integer EventType_2 = 2;
    // 发送群聊消息
    public static final Integer EventType_3 = 3;



    // 用户上线。客户端登录
    public static final Integer EventType_4 = 4;
    // 回复客户端登录成功
    public static final Integer EventType_5 = 5;
    // 回复客户端登录失败
    public static final Integer EventType_6 = 6;


    //用户每次当ws打开后拉取消息事件 拉取未读消息
    public static final Integer EventType_7 = 7;
    // 消息签收
    public static final Integer EventType_8 = 8;
    // 获取联系人页面纪录
    public static final Integer EventType_9 = 9;
    // 拉取指定好友的聊天消息
    public static final Integer EventType_10 = 10;
    // 客户端发送消息后 服务端ack
    public static final Integer EventType_11 = 11;
    // 收到联系人消息
    public static final Integer EventType_12 = 12;

    // webRtc 信令服务器通道
    public static final Integer EventType_13 = 13;
    // 拉取好友列表
    public static final Integer EventType_14 = 14;

}
