package com.bill.remoting.transport.netty.server;

import com.bill.config.ServiceConfig;
import com.bill.factory.SingletonFactory;
import com.bill.provider.ServiceProvider;
import com.bill.provider.impl.ServiceProviderImpl;
import com.bill.utils.ThreadPoolFactoryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NettyServer {
    private final int port;
    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

    public NettyServer() {
        this.port = 9989;
    }

    public NettyServer(int port) {
        this.port = port;
    }

    public void registerService(ServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    public void run() {
        //新建工作线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("v1", false)
        );

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
//                开启nagle算法
                .childOption(ChannelOption.TCP_NODELAY, true)
//                开启TCP底层心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                        socketChannel.pipeline().addLast(new StringDecoder());
                        socketChannel.pipeline().addLast(eventExecutorGroup, new MyServerHandler());
                    }
                });
        //绑定端口
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("future information : [{}]", future.toString());
            System.out.println("绑定端口");
            future.addListener((future1 -> {
                if (future1.isSuccess()) {
                    System.out.println("监听成功");
                } else
                    System.out.println("监听失败");
            }));
            //等待服务端监听端口关闭
//            future.await();
            future.channel().close().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭工作线程
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
