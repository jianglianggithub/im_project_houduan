package com.jl.netty.handle;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.jl.entity.Msg;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
public class NettyInitHandle extends ChannelInitializer<SocketChannel> {


    @Autowired
    WebSocketParamHandle webSocketParamHandle;
    @Autowired
    WebSocketHandle webSocketHandle;
    SSLEngine sslEngine;

    public NettyInitHandle() throws Exception {
//        KeyStore ks = KeyStore.getInstance("JKS");
//        InputStream ksInputStream = new FileInputStream("/Users/keyi/Desktop/zhengshu/jiang.liang.com.keystore");
//        ks.load(ksInputStream, "123456".toCharArray());
//        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        kmf.init(ks, "123456".toCharArray());
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(kmf.getKeyManagers(), null, null);
//        SSLEngine sslEngine = sslContext.createSSLEngine();
//        sslEngine.setUseClientMode(false);
//        sslEngine.setNeedClientAuth(false);
//        this.sslEngine = sslEngine;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

   //     channel.pipeline().addFirst("ssl", new SslHandler(sslEngine));

        channel.pipeline()
                .addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(1024*64))
                .addLast(new ChunkedWriteHandler())
                .addLast(new IdleStateHandler(0, 0, 5, TimeUnit.MINUTES))// 默认5分钟
                .addLast(new WebSocketServerCompressionHandler())
                .addLast(webSocketParamHandle)
                .addLast(new WebSocketServerProtocolHandler("/ws",null,true,1024*64))
                .addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                        ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
                        objs.add(buf);
                        buf.retain();
                    }
                })
                .addLast(new MessageToMessageEncoder<MessageLiteOrBuilder>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                        ByteBuf result = null;
                        if (msg instanceof MessageLite) {
                            result = Unpooled.wrappedBuffer(((MessageLite) msg).toByteArray());
                        }
                        if (msg instanceof MessageLite.Builder) {
                            result = Unpooled.wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
                        }
                        WebSocketFrame frame = new BinaryWebSocketFrame(result);
                        out.add(frame);
                    }
                })
        .addLast(new ProtobufDecoder(Msg.Model.getDefaultInstance()))
        // 业务处理器
        .addLast(webSocketHandle);
    }
}
