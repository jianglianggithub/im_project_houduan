package com.jl.common.dao;

import com.jl.common.entity.ChatRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jl.common.vo.UnreadMessageVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Xiao
 * @since 2020-10-12
 */
public interface ChatRecordMapper extends BaseMapper<ChatRecord> {

    List<UnreadMessageVo> recoveryFriendRecord(
            @Param("uid") String uid,@Param("receiver")  String[] receiver);
}
