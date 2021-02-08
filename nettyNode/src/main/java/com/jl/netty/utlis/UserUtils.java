package com.jl.netty.utlis;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.nio.channels.Channel;

public class UserUtils {



    public static String getChannelUid(ChannelHandlerContext ctx) {
        return (String) ctx.attr(AttributeKey.valueOf("userId")).get();
    }


    public static boolean getChannelIsReset(ChannelHandlerContext ctx) {
        return ctx.attr(AttributeKey.valueOf("reset")).get() != null;
    }
}
