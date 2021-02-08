package com.jl.common.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jl.common.entity.UserFirends;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jl.common.vo.FriendAndFriendInfoVo;
import com.jl.common.vo.MailListVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Xiao
 * @since 2020-10-12
 */
public interface UserFirendsMapper extends BaseMapper<UserFirends> {


    List<FriendAndFriendInfoVo> getFriends( String userId);

    List<MailListVo> getMailList(String userId);

}
