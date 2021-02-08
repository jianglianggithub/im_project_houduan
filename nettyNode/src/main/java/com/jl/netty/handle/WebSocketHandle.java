package com.jl.netty.handle;

import com.jl.entity.Msg;
import com.jl.netty.manager.StateManager;
import com.jl.netty.processor.Processor;
import com.jl.netty.service.EventService;
import com.jl.netty.utlis.UserUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;


@Component
@Slf4j
@ChannelHandler.Sharable
public class WebSocketHandle extends SimpleChannelInboundHandler<Msg.Model> {

    @Autowired
    StateManager stateManager;

    @Autowired
    List<Processor> processors;

    @Autowired
    EventService eventService;

    //固定线程池【固定长度】
    private Scheduler scheduler = Schedulers.newParallel("netty-scheduler", 4);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg.Model o) throws Exception {


        Flux
                .fromIterable(processors)
                .filter(processor -> processor.test(o.getEvent()))
                .next()
                .subscribeOn(scheduler)
                .subscribe(processor -> processor.handle(UserUtils.getChannelUid(ctx),o,ctx.channel()));

    }


    /* close 触发的回调 */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("通道关闭事件 ctx {}",ctx.channel().remoteAddress());

        removeCache(ctx);
        super.channelInactive(ctx);
    }

    private void removeCache(ChannelHandlerContext ctx) {
        String userId = UserUtils.getChannelUid(ctx);
        if (userId != null) {

            // 非覆盖登录才需要 删除前用户登录的redis 因为 代表下线了。
            //  覆盖登录则不需要
            if (
                    !UserUtils.getChannelIsReset(ctx)
            ) {
                eventService.removeRedisCache(userId);
                stateManager.removeUser(userId);
            }



        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof  IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.ALL_IDLE) {
                log.info("客户端读写空闲 断开连接 client {}",ctx.channel().remoteAddress());
                removeCache(ctx);
                ctx.channel().close();
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
