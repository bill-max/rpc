package netty;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;

public class MyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NettyRequest request = (NettyRequest) msg;
        NettyResponse response=NettyResponse.builder().message("sever received message ++ "+request.getMethodName()).build();
        ChannelFuture channelFuture = ctx.writeAndFlush(response);
        channelFuture.addListener(ChannelFutureListener.CLOSE);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error==" + cause.getMessage());
        ctx.close();
    }
}
