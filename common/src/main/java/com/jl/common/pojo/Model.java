package com.jl.common.pojo;


import lombok.Data;

/**
 *  消息在kafka 的载体。
 */
@Data
public class Model {

    Integer event ;
    String chatRecordId;
}
