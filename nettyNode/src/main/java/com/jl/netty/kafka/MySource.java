package com.jl.netty.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface MySource {

    String myOutput = "myOutput";   //管道名称为"myOutput"  这个相当于 配置文件中的key  指定 map 中的哪一份配置文件
    String myInput = "myInput";

    @Output(myOutput)
    MessageChannel output();
    @Input(myInput)
    SubscribableChannel input();
}
