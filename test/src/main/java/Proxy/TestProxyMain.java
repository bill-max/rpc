package Proxy;

import Proxy.impl.ServiceImpl;

public class TestProxyMain {
    public static void main(String[] args) {
//        Service proxy = (Service) TestProxyFactory.getProxy(new ServiceImpl());
        ClientProxy clientProxy = new ClientProxy();
        Service proxy = clientProxy.getProxy(Service.class);
        System.out.println("result: "+ proxy.send("message"));
    }
}
