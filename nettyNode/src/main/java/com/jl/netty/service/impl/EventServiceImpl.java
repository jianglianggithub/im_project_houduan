package com.jl.netty.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.google.protobuf.ByteString;
import com.jl.common.dao.UserFirendsMapper;
import com.jl.common.entity.ChatRecord;
import com.jl.common.entity.UserInfo;
import com.jl.common.service.ChatRecordService;
import com.jl.common.service.UserInfoService;
import com.jl.common.utils.ChineseCharacter;
import com.jl.common.vo.FriendAndFriendInfoVo;
import com.jl.common.vo.FriendsVo;
import com.jl.common.vo.MailListVo;
import com.jl.common.vo.UnreadMessageVo;
import com.jl.entity.EventType;
import com.jl.common.pojo.Model;
import com.jl.entity.Msg;
import com.jl.entity.WebRtcWrapper;
import com.jl.netty.kafka.MySource;
import com.jl.netty.manager.RedisUtil;
import com.jl.netty.manager.StateManager;
import com.jl.netty.service.EventService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.*;


@Service
@Slf4j
public class EventServiceImpl implements EventService {

    @Autowired
    StateManager stateManager;

    @Autowired
    RedisUtil redisUtil;

    @Value("${const.userInfoCacheRedisKey}")
    String userInfoCacheRedisKey;

    public static void main(String[] args) {

    }
    @Autowired
    MySource mySource;

    @Autowired
    ChatRecordService chatRecordService;

    @Autowired
    UserInfoService userInfoService;



    @Override
    public void loginSuccessAck(Channel channel,String uid) {
        Msg.Model model = buildMsgBody(EventType.EventType_5, "登录成功");
        log.info("回复登录上线成功通知成功 uid = {}",uid);
        channel.writeAndFlush(model);
    }


    private Msg.Model buildMsgBody(Integer eventType,String msg) {
        Msg.Model model = Msg.Model.newBuilder()
                .setEvent(eventType)
                .setContent(ByteString.copyFrom(msg.getBytes())).build();
        return model;
    }

    @Override
    public void loginFailAck(Channel channel, String uid) {
        Msg.Model model = buildMsgBody(EventType.EventType_6,"登录失败");
        log.error("获取本地ip失败 用户登录失败 uid = {} address = {}",uid,channel.remoteAddress());
        channel.writeAndFlush(model);
    }

    @Override
    public void sendMsg(Model model) {


        ChatRecord record = chatRecordService.getMsgById(model.getChatRecordId());
        String receiveUid = record.getReceiveUid();
        Channel channel = stateManager.getChannelIsActive(receiveUid);
        if (channel == null) {
            log.error("接收消息用户 通道已关闭 无法接收消息 receiveId = {} ",receiveUid);
            // 用户不存在 将消息 当前不存在可能是 发送的时候 用户在redis 中是在线的。 但是经过kafka 派发后
            // 用户下线 或者 websocket 连接断开了 所以导致的。

            // 目前如果消息 发送不了 不做处理。 因为上线后自动 通过 http协议拉取 未读取的消息 然后换成到本地即可
            // 通过消息id 进行去重 如果下发了的话 也无所谓。 然后通过时间排序即可。
            return;
        }
        Msg.Model model2receive = buildMsgBody(EventType.EventType_12, JSON.toJSONString(record));
        channel.writeAndFlush(model2receive);
    }

    @Override
    public void sendMsg(String userId, WebRtcWrapper webRtcWrapper) {
        Channel channel = stateManager.getChannelIsActive(userId);
        Msg.Model model = buildMsgBody(EventType.EventType_13, JSON.toJSONString(webRtcWrapper));

        channel.writeAndFlush(model);
    }

    // 预发送消息 还未经过kafka派发
    @Override
    public void preSendMsg(String uid, Msg.Model msg,Channel channel) {


        // 获取用户对应的netty节点 在kafka监听的 tag
        String nodeUUID = (String) redisUtil.hget(userInfoCacheRedisKey, uid);

        String sendUid = msg.getSendUid();
        String receiver = msg.getReceiver();
        // 添加消息记录到db
        String recordId = chatRecordService.insertMsgRecord(
                msg.getMessageId(),
                uid,
                receiver,
                msg.getContent().toString(Charset.defaultCharset()),
                msg.getSendTime()
        );
        Model model = new Model();
        model.setEvent(msg.getEvent());
        model.setChatRecordId(recordId);

        boolean sendStat = false;

        if (nodeUUID != null) {
            sendStat = mySource.output().send(
                            MessageBuilder
                            .withPayload(JSON.toJSONString(model))
                            .setHeader("tag", nodeUUID)
                            .build()
            );
            log.info(
                    "消息发送到kafka 结果 = {} 消息id {} sendId = {} " +
                            "recordId = {}, 接收方用户所在节点监听topic tag = {}",
                    sendStat,
                    recordId,
                    sendUid,
                    receiver,
                    nodeUUID
            );
        } else {
            //如果发送人不存在的话 暂时不做处理 代表这个人没上线 还是跟 派发后 用户下线一个道理 上线后垃圾
            log.error(
                    "消息发送到kafka 结果 = {} 消息id {} sendId = {} " +
                            "recordId = {}, 接收方用户所在节点监听topic tag = {}",
                    sendStat,
                    recordId,
                    sendUid,
                    receiver,
                    nodeUUID
            );
        }
        // 回复客户端发送消息成功
        Msg.Model ackModel = buildMsgBody(EventType.EventType_11, buildAckSendMessage(msg.getMessageId(), msg.getReceiver()));
        channel.writeAndFlush(ackModel);


    }

    private String buildAckSendMessage(String messageId, String receiver) {
        Map<String,String> result = new HashMap<>();
        result.put("messageId",messageId);
        result.put("firendId",receiver);
        return JSON.toJSONString(result);
    }



    @Override
    public void removeRedisCache(String userId) {
        redisUtil.hdel(userInfoCacheRedisKey,userId);
    }

    @Override
    public void unReadMsg(String uid, Channel channel) {
        List<UnreadMessageVo> vo = userInfoService.getUnreadMessageList(uid);
        if (vo.size() > 0) {
            Msg.Model model = buildMsgBody(EventType.EventType_7, JSON.toJSONStringWithDateFormat(vo,"yyyy-MM-dd HH:mm:ss"));
            channel.writeAndFlush(model);
        }
    }


    @Override
    public void ackMeessage(String[] ids) {
        chatRecordService.ackMeessage(ids);
    }

    @Autowired
    UserFirendsMapper userFirendsMapper;

    @Override
    public void contactsRecord(String uid, Channel channel) {
        List<FriendAndFriendInfoVo> friends = userFirendsMapper.getFriends(uid);
        Msg.Model model = buildMsgBody(EventType.EventType_9, JSON.toJSONStringWithDateFormat(friends,"yyyy-MM-dd HH:mm:ss"));

        channel.writeAndFlush(model);
    }

    @Override
    public void recoveryFriendRecord(String uid, String receiver, Channel channel) {

        List<UnreadMessageVo> msg = chatRecordService.recoveryFriendRecord(uid,receiver);
        Msg.Model model = buildMsgBody(EventType.EventType_10, JSON.toJSONStringWithDateFormat(msg,
                "yyyy-MM-dd HH:mm:ss"));
        channel.writeAndFlush(model);
    }

    @Override
    public void CallFailed(Channel channel) {

        WebRtcWrapper init = WebRtcWrapper.getInit();
        init.setEventType(WebRtcWrapper.type_3);
        WebRtcWrapper.Action action = init.getAction();
        action.setEventType(WebRtcWrapper.Action.type_3);
        action.setMsg("对方拒绝接听或者不在线");
        init.setAction(action);

        Msg.Model model = buildMsgBody(EventType.EventType_13, JSON.toJSONString(init));
        channel.writeAndFlush(model);
    }

    @Override
    public void call(Channel channel,String uid,String to) {

        WebRtcWrapper init = WebRtcWrapper.getInit();
        init.setEventType(WebRtcWrapper.type_3);
        WebRtcWrapper.Action action = init.getAction();
        action.setEventType(WebRtcWrapper.Action.type_2);
        action.setFrom(uid);
        action.setTo(to);
        Msg.Model model = buildMsgBody(EventType.EventType_13, JSON.toJSONString(init));
        channel.writeAndFlush(model);

    }



    @Override
    public void friendsGet(String uid, Channel channel) {
        List<MailListVo> mailList = userFirendsMapper.getMailList(uid);

        FriendsVo friendsVo = new FriendsVo();

        Map<String,List<MailListVo>> result = new TreeMap<>();
        // 获取好友名字 开头一个文字 并且转拼音进行分类
        for (MailListVo mailListVo : mailList) {
            // 好友信息
            UserInfo userInfo = mailListVo.getUserInfo();
            String likeName = userInfo.getLikeName();
            if (!StringUtils.isEmpty(likeName)) {

                char oneChar = likeName.toCharArray()[0];
                if(ChineseCharacter.check(oneChar)) {
                    // 第一个文字 拼音
                    String pinyin = PinyinHelper.toPinyin( oneChar + "", PinyinStyleEnum.NORMAL);

                    List<MailListVo> mailListVos = result.computeIfAbsent(pinyin.toCharArray()[0] + ""
                            ,key -> new ArrayList<>());
                    mailListVos.add(mailListVo);
                } else {
                    // 如果不是汉字 判断是否为字母
                    if (ChineseCharacter.checkZM(oneChar)) {
                        result.computeIfAbsent((oneChar + "").toLowerCase(),key -> new ArrayList<>()).add(mailListVo);
                    }else {
                        result.computeIfAbsent("#",key -> new ArrayList<>()).add(mailListVo);
                    }

                }

            }
        }

        friendsVo.setResult(result);
        friendsVo.setInitials(result.keySet());

        Msg.Model model = buildMsgBody(EventType.EventType_14,JSON.toJSONString(friendsVo));
        channel.writeAndFlush(model);

    }

    @Override
    public void closeMedia(String uid) {
        Channel closeEventUser = stateManager.getChannelIsActive(uid);

        WebRtcWrapper init = WebRtcWrapper.getInit();
        init.setEventType(WebRtcWrapper.type_5);

        Msg.Model model = buildMsgBody(EventType.EventType_13,JSON.toJSONString(init));
        closeEventUser.writeAndFlush(model);
    }


}
