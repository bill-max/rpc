package com.bill.remoting.transport.netty.client;

import com.bill.factory.SingletonFactory;
import com.bill.remoting.constants.RpcConstants;
import com.bill.remoting.dto.Message;
import com.bill.remoting.dto.Response;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyClientHandler extends ChannelInboundHandlerAdapter {
    private final Process process;
    private final NettyClient nettyClient;

    public MyClientHandler() {
        this.process = SingletonFactory.getInstance(Process.class);
        this.nettyClient = SingletonFactory.getInstance(NettyClient.class);
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        //客户端发送消息
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("send message", CharsetUtil.UTF_8));
    }
    /**
     * Read the message transmitted by the server
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) {
        try {
            //客户端接收服务端消息
            log.info("client receive message => {}", o.toString());
            if (o instanceof Message msg) {
                byte messageType = msg.getMessageType();
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                    log.info("heart [{}]", msg.getData());
                } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                    Response<Object> data = (Response<Object>) msg.getData();
                    process.process(data);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            ReferenceCountUtil.release(o);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
