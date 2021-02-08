package com.jl.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Xiao
 * @since 2020-10-12
 */
@Data
public class UserInfo extends Model<UserInfo> {

    private static final long serialVersionUID = 1L;

    @TableId( type= IdType.UUID)
    private String id;

    private String uname;

    private String pwd;

    private String image;

    private LocalDateTime lastLoginTime;

    private LocalDate birthday;

    private String likeName;
}
