package Proxy;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Getter
@Setter
public class ClientProxy implements InvocationHandler {


    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        System.out.println("get proxy ====> " + clazz.toString());
        System.out.println(Arrays.toString(clazz.getMethods()));
        System.out.println(clazz.getSimpleName());
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
        System.out.println("get pro==> " + o);
        return (T) o;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoked method: [{}]", method.getName());
        log.info("method  =>> " + method.getDeclaringClass().toString());

        return "重写方法";
    }
}
