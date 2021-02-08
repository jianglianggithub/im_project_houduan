package com.jl.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jl.common.cst.Const;
import com.jl.common.entity.ChatRecord;
import com.jl.common.dao.ChatRecordMapper;
import com.jl.common.pojo.Model;
import com.jl.common.service.ChatRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jl.common.utils.DateUtils;
import com.jl.common.vo.UnreadMessageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Xiao
 * @since 2020-10-12
 */
@Service
public class ChatRecordServiceImpl  implements ChatRecordService {

    @Autowired
    ChatRecordMapper chatRecordMapper;

    @Override
    public String insertMsgRecord(String messageId,String sendUid, String receiver, String content,long sendTime) {
        ChatRecord record = new ChatRecord();
        record.setId(messageId);
        record.setSendUid(sendUid);
        record.setReceiveUid(receiver);
        record.setAck(Const.CHAT_RECORD_ACK_0);
        record.setSendTime(sendTime);
        record.setMsgBody(content);
        record.setMsgType(Const.CHAT_RECORD_MSG_TYPE_1);
        chatRecordMapper.insert(record);
        return record.getId();
    }

    @Override
    public ChatRecord getMsgById(String chatRecordId) {

        return chatRecordMapper.selectById(chatRecordId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ackMeessage(String[] ids) {
        for (String id : ids) {
            ChatRecord record = new ChatRecord();
            record.setAck(1);
            record.setAckTime(LocalDateTime.now());
            record.setId(id);
            record.updateById();
        }
    }

    @Override
    public List<UnreadMessageVo> recoveryFriendRecord(String uid, String receiver) {

        List<UnreadMessageVo> chatRecords = chatRecordMapper
                .recoveryFriendRecord(uid,receiver.split(","));
        return chatRecords;

    }
}
