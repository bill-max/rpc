package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;

public class NettyClient {
    private final String host;
    private final int port;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        //创建nioEventLoop
        EventLoopGroup eventExecutors = new NioEventLoopGroup();
        //创建bootstrap
        bootstrap = new Bootstrap();
        //配置参数
        //设置线程组
        bootstrap.group(eventExecutors)
                .handler(new LoggingHandler(LogLevel.INFO))
                //设置通道实现类型
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //初始化通道
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //自定义序列化编解码器
                        socketChannel.pipeline().addLast(new MyClientHandler());
                    }
                });
    }

    public NettyResponse sendMessage(NettyRequest request) {
        try {
            //创建链接
            ChannelFuture future = bootstrap.connect(host, port).sync();
            System.out.println("客户端链接成功");
            //监听服务端返回数据
            Channel channel = future.channel();
            if (channel != null) {
                channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            System.out.println("success : " + request.toString());
                        } else {
                            System.out.println("send fail  " + channelFuture.cause());
                        }
                    }
                });
                //阻塞等待，直到服务器关闭
                channel.close().sync();
                AttributeKey<NettyResponse> response = AttributeKey.valueOf("response");
                return channel.attr(response).get();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


}
