package Proxy;

import java.lang.reflect.Proxy;

public class TestProxyFactory {
    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),//目标对象的类加载器
                target.getClass().getInterfaces(),//需要实现的接口
                new TestInvocationHandler(target)
        );
    }
}
