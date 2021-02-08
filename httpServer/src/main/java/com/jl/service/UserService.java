package com.jl.service;

import com.jl.common.api.CommonListResult;
import com.jl.common.api.CommonResult;
import com.jl.common.vo.PageKit;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

public interface UserService {
    CommonResult<?> register(String username1, String s, MultipartFile file, String username, String password) throws IOException, ParseException;

    CommonListResult<?> getFriends(String userId, PageKit pageKit);

    CommonResult<?> getMailList(String userId);

    CommonResult<?> getUserInfoByToken(String token);

    CommonResult<?> token(String userId);

    CommonResult<?> getMessageList(String userId, String friendId);

    CommonResult<?> getUnreadMessageList(String userId, String friendId);
}

