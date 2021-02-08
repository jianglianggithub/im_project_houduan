package com.jl.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jl.common.entity.UserFirends;
import com.jl.common.entity.UserInfo;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class FriendAndFriendInfoVo {


    private String firendId;
    private String likeName;
    private String image;

    private String lastMsg;


    private Long lastTime;
}
