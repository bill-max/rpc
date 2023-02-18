package Proxy;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

@Getter
@Setter
public class TestInvocationHandler implements InvocationHandler {
    private final Object target;

    public TestInvocationHandler(Object target) {
        this.target = target;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("invoked method: [{}]"+method.getName());
        System.out.println("method  =>> " + method.toString());
        return null;
    }
}
