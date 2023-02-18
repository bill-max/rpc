package com.bill.test;

import com.bill.config.ServiceConfig;
import com.bill.proxy.ClientProxy;
import com.bill.remoting.transport.socket.SocketClient;

public class TestClient {
    public static void main(String[] args) {
        DemoRpcService service1 = new DemoRpcServiceImpl();
        ServiceConfig serviceConfig = ServiceConfig
                .builder()
                .group("")
                .version("")
                .service(service1)
                .build();
        ClientProxy clientProxy = new ClientProxy(new SocketClient(), serviceConfig);
        DemoRpcService proxy = clientProxy.getProxy(DemoRpcService.class);
        String qqq = proxy.hello(new Hello("qqq"));
        System.out.println(qqq);
    }
}
