package com.jl.controller;


import com.jl.common.api.CommonListResult;
import com.jl.common.api.CommonResult;
import com.jl.common.api.CustomException;
import com.jl.common.vo.PageKit;
import com.jl.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {


    @Autowired
    UserService userService;

    @RequestMapping("/info")
    public CommonResult<?> info() {
        return userService.token(super.getUserId());
    }

    @RequestMapping("/getUserInfoByToken")
    public CommonResult<?> getUserInfoByToken(String token) {
        return userService.getUserInfoByToken(token);
    }

    /**
     注册
     */
    @RequestMapping("/register")
    public CommonResult<?> register(String username, String password, MultipartFile file,String calendar,String name) throws IOException, ParseException {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password))
            throw new CustomException("用户名或者密码不能为空");
        return userService.register(username,password,file,calendar,name);
    }

    /**
      获取联系人页面数据 包括lastmsg
     */
    @RequestMapping("/getFriendsAndRecord")
    public CommonListResult<?> getFriends(PageKit pageKit) {
        return userService.getFriends(super.getUserId(),pageKit);
    }


    /**
     * 获取通讯录好友列表 并且 获取好友列表首字母
     */
    @RequestMapping("/getMailList")
    public CommonResult<?> getMailList() {
        return userService.getMailList(super.getUserId());
    }

    /**
     * 获取当前用户和对方的未读的消息记录
     *  暂时不支持 登录后把所有未读的消息全部 恢复 做法类似微信
     *  消息尽可能全部缓存的客户端的localstore 来实现
     */
    @RequestMapping("/getMessageList")
    public CommonResult<?> getUnreadMessageList(String userid) {
        return userService.getUnreadMessageList(userid,userid);
    }


}
