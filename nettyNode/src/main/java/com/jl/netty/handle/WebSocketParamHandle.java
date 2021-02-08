package com.jl.netty.handle;

import com.jl.common.api.CommonResult;
import com.jl.common.utils.jwt.JwtHelper;
import com.jl.feign.UserApi;
import com.jl.netty.manager.StateManager;
import io.jsonwebtoken.Claims;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@Slf4j
@ChannelHandler.Sharable
public class WebSocketParamHandle extends ChannelInboundHandlerAdapter {


    @Autowired
    StateManager stateManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {

            FullHttpRequest request = (FullHttpRequest) msg;
            String origin = request.headers().get("Origin");
            String uri = request.uri();
            if (null == origin) {
                log.info("通道关闭");
                ctx.close();
            } else {
                if (null != uri && uri.contains("/ws") && uri.contains("?")) {
                    String[] uriArray = uri.split("\\?");
                    if (null != uriArray && uriArray.length > 1) {
                        String[] paramsArray = uriArray[1].split("=");
                        if (null != paramsArray && paramsArray.length > 1) {
                            String token = paramsArray[1];
                            String uid = parseToken2UserId(token);
                            if (uid == null) {
                                log.info("token 不存在 通道关闭");
                                ctx.close().addListener( future -> {
                                    if (!future.isSuccess()) {
                                        log.error("close 失败 remoteAddress {}",ctx.channel().remoteAddress());
                                    }
                                });
                                return;
                            } else {
                                Attribute<String> uidAttr = ctx.attr(AttributeKey.valueOf("userId"));
                                uidAttr.set(uid);
                                log.info("通道上线 ctx remote addr {}",ctx.channel().remoteAddress());
                                // 上线改为 手动上线消息
                                // stateManager.addUser(uid,ctx.channel());
                            }
                        }
                    }
                    //重新设置请求地址
                    request.setUri("/ws");
            }
        }


    }
        super.channelRead(ctx, msg);
}

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserApi userApi;


    private String parseToken2UserId(String token) {
        CommonResult<String> result = userApi.getUserInfoByToken(token);
        String userId = result.getData();
        return userId;
    }
}
