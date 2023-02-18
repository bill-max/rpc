package com.bill.remoting.transport.socket;

import com.bill.config.ServiceConfig;
import com.bill.factory.SingletonFactory;
import com.bill.provider.ServiceProvider;
import com.bill.provider.impl.ServiceProviderImpl;
import com.bill.registry.Service;
import com.bill.registry.impl.ServiceImpl;
import com.bill.utils.ThreadPoolFactoryUtil;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;

public class SocketServer {

    public static final int PORT = 9989;

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;

    public SocketServer() {
        //初始化线程池工具类
        threadPool = ThreadPoolFactoryUtil.creatCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

    }

    public void start() {
        try (
                ServerSocket socket = new ServerSocket();
        ) {
            socket.bind(new InetSocketAddress(PORT));
            Socket accept = null;
            while ((accept = socket.accept()) != null) {
                threadPool.execute(new SocketRpcRequestHandlerRunnable(accept));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registryServer(ServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }
}
