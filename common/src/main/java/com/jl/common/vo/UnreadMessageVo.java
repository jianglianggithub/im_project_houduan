package com.jl.common.vo;


import com.jl.common.entity.ChatRecord;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UnreadMessageVo extends ChatRecord {

    // type 指定这条消息是当前用户是发送方还是 接收方 方便前端视图展示
    private String type;



}

