package com.jl.common.dao;

import com.jl.common.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jl.common.vo.UnreadMessageVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Xiao
 * @since 2020-10-12
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    List<UnreadMessageVo> getUnreadMessageList(String userId);

    List<?> getUnreadMessageList2(String userId);
}
