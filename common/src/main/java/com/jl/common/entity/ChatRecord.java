package com.jl.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Xiao
 * @since 2020-10-12
 */
@Data
public class ChatRecord extends Model<ChatRecord> {

    private static final long serialVersionUID = 1L;

    @TableId( type= IdType.ASSIGN_UUID)
    private String id;

    /**
     * 发送人id
     */
    private String sendUid;

    /**
     * 接收人id
     */
    private String receiveUid;

    /**
     * 是否确认收到 1 = 确认收到 0 = 未确认
     */
    private Integer ack;

    /**
     * 发送时间
     */
    private Long sendTime;

    /**
     * ack时间
     */
    private LocalDateTime ackTime;

    /**
     * 消息内容
     */
    private String msgBody;

    /**
     * 消息类型 1=文本
     */
    private Integer msgType;


}
