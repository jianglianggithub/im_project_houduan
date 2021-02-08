package com.jl.common.service;

import com.jl.common.entity.ChatRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jl.common.vo.UnreadMessageVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Xiao
 * @since 2020-10-12
 */
public interface ChatRecordService  {

    String insertMsgRecord(String messageId,String sendUid, String receiver, String content,long sendTime);

    ChatRecord getMsgById(String chatRecordId);

    void ackMeessage(String[] ids);

    List<UnreadMessageVo> recoveryFriendRecord(String uid, String receiver);
}
