package com.jl.common.generator;

import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 该类  处理 sqlServer 数据类型 转换问题【3.0以上的mybatis Plus 存在的问题】
 */
//@Component
public class MyLocalDateTimeTypeHandler extends LocalDateTimeTypeHandler {
    @Override
    public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        Object object = rs.getObject(columnName);
        if(object instanceof Timestamp){//在这里强行转换，将sql的时间转换为LocalDateTime
            return LocalDateTime//可以根据自己的需要进行转化
                    .ofInstant(((Timestamp)object).toInstant(), ZoneOffset.ofHours(0));
        }
        return super.getResult(rs, columnName);
    }

}
