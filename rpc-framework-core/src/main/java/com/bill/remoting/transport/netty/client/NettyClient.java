package com.bill.remoting.transport.netty.client;

import com.bill.factory.SingletonFactory;
import com.bill.loadBanlance.impl.RandomLoadBalance;
import com.bill.registry.Service;
import com.bill.registry.impl.ServiceImpl;
import com.bill.remoting.constants.RpcConstants;
import com.bill.remoting.dto.Message;
import com.bill.remoting.dto.Request;
import com.bill.remoting.dto.Response;
import com.bill.remoting.transport.RequestTransport;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class NettyClient implements RequestTransport {
    private final String host;
    private final int port;
    private static final Bootstrap bootstrap;

    private final Process process;

    private final ChannelProvider channelProvider;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.process = SingletonFactory.getInstance(Process.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
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

    public Response sendMessage(Request request) {
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
                AttributeKey<Response> response = AttributeKey.valueOf("response");
                return channel.attr(response).get();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Object sendRequest(Request request) {
        // build return value
        CompletableFuture<Response<Object>> resultFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("processing ----");
            return new Response<Object>();
        });
        // get server address
        Service service = new ServiceImpl();
        InetSocketAddress inetSocketAddress = service.lookupService(request, new RandomLoadBalance());
        // get channel
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            process.put(request.getRequestId(), resultFuture);
            Message message = Message.builder().messageType(RpcConstants.REQUEST_TYPE).data(request).compress((byte) 0x01).codec((byte) 0x01).build();
            channel.writeAndFlush(message);
        }
        return resultFuture;
    }

    private Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    private Channel doConnect(InetSocketAddress inetSocketAddress) {
        try {
            CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
            bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                    completableFuture.complete(future.channel());
                } else {
                    throw new IllegalStateException();
                }
            });
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
