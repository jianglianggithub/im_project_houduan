package com.jl.service.impl;



import com.google.common.collect.Maps;
import com.jl.common.api.CommonListResult;
import com.jl.common.api.CommonResult;
import com.jl.common.dao.UserFirendsMapper;
import com.jl.common.entity.UserFirends;
import com.jl.common.entity.UserInfo;
import com.jl.common.service.UserInfoService;
import com.jl.common.utils.ChineseCharacter;
import com.jl.common.vo.*;
import com.jl.register.config.UserExt;
import com.jl.service.FastDfsUtilService;
import com.jl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoService userInfoService;
    @Autowired
    FastDfsUtilService fastDfsUtilService;
    @Autowired
    UserFirendsMapper userFirendsMapper;

    @Override
    public CommonResult<?> register(String username, String password, MultipartFile file,String calendar,String name) throws IOException, ParseException {
        String url = fastDfsUtilService.upload(file);

        return userInfoService.register(username,password,url,calendar,name);
    }

    @Override
    public CommonListResult<?> getFriends(String userId, PageKit pageKit) {
        //Page<FriendAndFriendInfoVo> page = new Page<>(pageKit.getPage(),pageKit.getLimit());
        List<FriendAndFriendInfoVo> friends = userFirendsMapper.getFriends(userId);
        return CommonListResult.successList(friends);
    }

    @Override
    public CommonResult<?> getMailList(String userId) {
//        List<MailListVo> mailList = userFirendsMapper.getMailList(userId);
//
//        FriendsVo friendsVo = new FriendsVo();
//
//        Map<String,List<MailListVo>> result = new TreeMap<>();
//        // 获取好友名字 开头一个文字 并且转拼音进行分类
//        for (MailListVo mailListVo : mailList) {
//            // 好友信息
//            UserInfo userInfo = mailListVo.getUserInfo();
//            String likeName = userInfo.getLikeName();
//            if (!StringUtils.isEmpty(likeName)) {
//
//                char oneChar = likeName.toCharArray()[0];
//                if(ChineseCharacter.check(oneChar)) {
//                    // 第一个文字 拼音
//                    String pinyin = PinyinHelper.toPinyin( oneChar + "", PinyinStyleEnum.NORMAL);
//
//                    List<MailListVo> mailListVos = result.computeIfAbsent(pinyin.toCharArray()[0] + ""
//                            ,key -> new ArrayList<>());
//                    mailListVos.add(mailListVo);
//                } else {
//                    // 如果不是汉字 判断是否为字母
//                    if (ChineseCharacter.checkZM(oneChar)) {
//                        result.computeIfAbsent((oneChar + "").toLowerCase(),key -> new ArrayList<>()).add(mailListVo);
//                    }else {
//                        result.computeIfAbsent("#",key -> new ArrayList<>()).add(mailListVo);
//                    }
//
//                }
//
//            }
//        }
//
//        friendsVo.setResult(result);
//        friendsVo.setInitials(result.keySet());
        return CommonResult.success(null);
    }


    @Autowired
    TokenStore tokenStore;

    @Override
    public CommonResult<?> getUserInfoByToken(String token) {
        OAuth2Authentication authentication = tokenStore.readAuthentication(token.split(" ")[1]);
        String uid = null;
        if (authentication != null) {
            UserExt principal = (UserExt) authentication.getPrincipal();
            uid = principal.userId;
        }
        return CommonResult.success(uid,"");
    }

    @Override
    public CommonResult<?> token(String userId) {
        UserInfo info = userInfoService.getUserByUserId(userId);

        return CommonResult.success(info);
    }

    @Override
    public CommonResult<?> getMessageList(String userId, String friendId) {

        return null;
    }

    @Override
    public CommonResult<?> getUnreadMessageList(String userId, String friendId) {
        List<?> vo = userInfoService.getUnreadMessageList(userId);
        return CommonResult.success(vo);
    }


}
