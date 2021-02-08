package com.jl.common.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jl.common.api.CommonResult;
import com.jl.common.api.CustomException;
import com.jl.common.entity.UserInfo;
import com.jl.common.dao.UserInfoMapper;
import com.jl.common.service.UserInfoService;
import com.jl.common.utils.DateUtils;
import com.jl.common.vo.UnreadMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.nio.cs.US_ASCII;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Xiao
 * @since 2020-10-12
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public CommonResult<?> register(String username, String password, String url, String calendar, String name) throws ParseException {
        UserInfo userInfo1 = userInfoMapper.selectOne(
                new LambdaQueryWrapper<UserInfo>()
                        .eq(UserInfo::getUname, username)
        );

        if (userInfo1 != null) {
            throw new CustomException("用户名已存在");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUname(username);
        userInfo.setImage(url);
        userInfo.setPwd(SecureUtil.md5(password));
        userInfo.setLikeName(name);
        userInfo.insert();

        userInfo.setBirthday(DateUtils.formatterDateString(calendar));
        return CommonResult.success("注册成功");
    }

    @Override
    public UserInfo getUserByUserName(String username) {
        return userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUname,username));
    }

    @Override
    public UserInfo getUserByUserId(String userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);

        return userInfo;
    }

    @Override
    public List<UnreadMessageVo> getUnreadMessageList(String userId) {
        return userInfoMapper.getUnreadMessageList(userId);
    }
    @Override
    public List<?> getUnreadMessageList2(String userId) {
        return userInfoMapper.getUnreadMessageList2(userId);
    }
}
