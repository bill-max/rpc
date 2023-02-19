package com.bill.proxy;

import com.bill.config.ServiceConfig;
import com.bill.enums.ResponseCodeEnum;
import com.bill.remoting.dto.Request;
import com.bill.remoting.dto.Response;
import com.bill.remoting.transport.RequestTransport;
import com.bill.remoting.transport.netty.client.NettyClient;
import com.bill.remoting.transport.socket.SocketClient;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Getter
@Setter
public class ClientProxy implements InvocationHandler {
    private final RequestTransport rpcRequestTransport;

    private final ServiceConfig rpcServiceConfig;


    public ClientProxy(RequestTransport rpcRequestTransport, ServiceConfig rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    public ClientProxy(RequestTransport rpcRequestTransport) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = new ServiceConfig();
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
//        System.out.println("get proxy ====> " + clazz.toString());
//        System.out.println(Arrays.toString(clazz.getMethods()));
//        System.out.println(Arrays.toString(clazz.getDeclaredClasses()));
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
//        log.info("get pro==> {}", o);
        return (T) o;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoked method: [{}]", method.getName());
        System.out.println("method  =>> " + method.toString());
        if (method.getName().equals("toString")) {
            return toString();
        }
        Request rpcRequest = Request.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();

        System.out.println(rpcRequest.toString());
        Response<Object> response = null;
        //socket send request
        if (rpcRequestTransport instanceof SocketClient) {
            response = (Response<Object>) rpcRequestTransport.sendRequest(rpcRequest);
        }
        //todo netty send request
        if (rpcRequestTransport instanceof NettyClient) {
            CompletableFuture<Response<Object>> completableFuture = (CompletableFuture<Response<Object>>) rpcRequestTransport.sendRequest(rpcRequest);
            response = completableFuture.get();
        }
        if (response == null) {
            log.error("response is null");
            return null;
        }
        return response.getData();
    }

//    @SuppressWarnings("unchecked")
//    @SneakyThrows
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws RuntimeException {
//        System.out.println(" " + method.toString());
//        log.info("invoked method: [{}]", method.getName());
//        System.out.println(Arrays.toString(args));
////        return "method.invoke()";
//        Request request = Request.builder().methodName(method.getName())
//                .interfaceName(method.getDeclaringClass().getName())
//                .paramTypes(method.getParameterTypes())
//                .group(rpcServiceConfig.getGroup())
//                .version(rpcServiceConfig.getVersion())
//                .requestId(UUID.randomUUID().toString())
//                .parameters(args)
//                .build();
//        Response<Object> response = null;
//        if (rpcRequestTransport instanceof SocketClient) {
//            System.out.println("==================");
//            System.out.println(request.toString());
//            response = (Response<Object>) rpcRequestTransport.sendRequest(request);
//        }
//        if (response == null) {
//            return Response.fail(ResponseCodeEnum.FAIL);
//        }
//        log.info("data===============::::" + response);
//        return response.getData();
//    }
}
