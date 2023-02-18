package netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

public class MyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        //客户端发送消息
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("send message", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //客户端接收服务端消息
        NettyResponse response= (NettyResponse) o;
        //结果存入attributeMap
        AttributeKey<NettyResponse> attributeKey = AttributeKey.valueOf("response");
        channelHandlerContext.attr(attributeKey).set(response);
        channelHandlerContext.close().sync();
    }
}
