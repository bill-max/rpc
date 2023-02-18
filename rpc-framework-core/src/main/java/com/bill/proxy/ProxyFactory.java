package com.bill.proxy;

import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ProxyFactory {

    public static Object getProxy(Object clazz,ClientProxy clientProxy) {
        System.out.println(clazz.toString());
        System.out.println(Arrays.toString(clazz.getClass().getInterfaces()));
        return Proxy.newProxyInstance(
                clazz.getClass().getClassLoader(),
                clazz.getClass().getInterfaces(),
                clientProxy
        );
    }
}
