package com.jl.netty;


import com.jl.netty.handle.NettyInitHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Slf4j
public class NettyWebSocketServer {

    @Autowired
    NettyInitHandle nettyInitHandle;

    @Value("${im.webSocketPort}")
    Integer port;

    EventLoopGroup bos;
    EventLoopGroup worker;
    ServerBootstrap serverBootstrap;
    ChannelFuture channelFuture;
    @PostConstruct
    public void startNettyServer() throws InterruptedException {
        // 每次创建byteBuf 都检测是否有内存泄漏风险
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);

        bos = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup(2);
        serverBootstrap = new ServerBootstrap();

        serverBootstrap.channel(NioServerSocketChannel.class)
                .group(bos,worker)
                .handler(new LoggingHandler(LogLevel.TRACE))
                .childHandler(nettyInitHandle);
        channelFuture = serverBootstrap.bind(port).sync();
       log.info("netty websocket server start success port {}",port);
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        channelFuture.channel().close().sync();
        bos.shutdownGracefully();
        worker.shutdownGracefully();
    }


}
