package com.bill.remoting.transport.netty.server;

import com.bill.enums.ResponseCodeEnum;
import com.bill.factory.SingletonFactory;
import com.bill.remoting.constants.RpcConstants;
import com.bill.remoting.dto.Message;
import com.bill.remoting.dto.Request;
import com.bill.remoting.dto.Response;
import com.bill.remoting.handler.RequestHandler;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Customize the ChannelHandler of the server to process the data sent by the client.
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放 ，{@link SimpleChannelInboundHandler} 内部的
 * channelRead 方法会替你释放 ByteBuf ，避免可能导致的内存泄露问题。
 */
@Slf4j
public class MyServerHandler extends ChannelInboundHandlerAdapter {
    private final RequestHandler requestHandler;

    public MyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }


    /**
     * 处理流程：判断输入消息类型  =》》 requestHandler.handle(request) =》》构建response =》》 message.setData(response) =》》 ctx.writeAndFlush(message)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof Message message) {
                log.info("netty server channel read ==> [{}]", message);
                byte messageType = message.getMessageType();
                Message m = new Message();
                m.setCompress(message.getCompress());
                m.setCodec(message.getCodec());
                //链接检测
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    message.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    message.setData(RpcConstants.PONG);
                } else {
                    Request request = (Request) message.getData();
                    Object result = requestHandler.handle(request);
                    log.info("chanel get result ==> {}", result.toString());
                    message.setMessageType(RpcConstants.RESPONSE_TYPE);
                    //当前管道存活且支持写入
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        Response<Object> response = Response.success(result, request.getRequestId());
                        message.setData(response);
                    } else {
                        Response<Object> fail = Response.fail(ResponseCodeEnum.FAIL);
                        message.setData(fail);
                        log.error("channel 写 时错误");
                    }
                }
                ctx.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error==" + cause.getMessage());
        ctx.close();
    }
}
