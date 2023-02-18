package allTest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 使用 Netty 实现服务端
 *
 * @author <a href="mailto:410486047@qq.com">Grey</a>
 * @date 2022/9/12
 * @since
 */
public class NettyServer {
    public static void main(String[] args) {
        // 引导服务端的启动
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 用于监听端口，接收新连接的线程组
        NioEventLoopGroup boss = new NioEventLoopGroup();
        // 表示处理每一个连接的数据读写的线程组
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap.group(boss, worker)
                // 指定IO模型为NIO
                .channel(NioServerSocketChannel.class)
                // 定义后面每一个连接的数据读写
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        System.out.println("服务启动中......");
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                // 本地绑定一个8000端口启动服务端
                .bind(8000);
    }
}

