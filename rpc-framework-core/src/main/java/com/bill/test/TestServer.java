package com.bill.test;

import com.bill.config.ServiceConfig;
import com.bill.remoting.transport.socket.SocketServer;

public class TestServer {
    public static void main(String[] args) {
        DemoRpcService service1 = new DemoRpcServiceImpl();
        ServiceConfig serviceConfig = ServiceConfig
                .builder()
                .group("")
                .version("")
                .service(service1)
                .build();
        SocketServer socketServer = new SocketServer();
        socketServer.registryServer(serviceConfig);
        socketServer.start();
    }
}
