package com.jl.common.service;

import com.jl.common.api.CommonResult;
import com.jl.common.entity.UserInfo;
import com.jl.common.vo.UnreadMessageVo;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Xiao
 * @since 2020-10-12
 */
public interface UserInfoService {

    CommonResult<?> register(String username, String password, String file, String calendar, String name) throws ParseException;

    UserInfo getUserByUserName(String username);

    UserInfo getUserByUserId(String userId);

    List<UnreadMessageVo> getUnreadMessageList(String userId);
    List<?> getUnreadMessageList2(String userId);
}
