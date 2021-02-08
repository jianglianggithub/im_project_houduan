package com.jl.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author Xiao
 * @since 2020-10-12
 */
@Data
public class UserFirends extends Model<UserFirends> {

    private static final long serialVersionUID = 1L;
    @TableId( type= IdType.UUID)
    private String id;

    /**
     * 用户id
     */
    private String uid;

    private String firendId;

    private String extName;

    private LocalDateTime addTime;

    private LocalDateTime removeTime;

    private Integer state;
}
