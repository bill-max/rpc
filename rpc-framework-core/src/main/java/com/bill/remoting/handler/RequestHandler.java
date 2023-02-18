package com.bill.remoting.handler;

import com.bill.factory.SingletonFactory;
import com.bill.provider.ServiceProvider;
import com.bill.provider.impl.ServiceProviderImpl;
import com.bill.remoting.dto.Request;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//todo handler

/**
 * 处理request
 */
@Slf4j
public class RequestHandler {
    private final ServiceProvider serviceProvider;

    public RequestHandler() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    public Object handle(Request request) {
        System.out.println("server handle==========>");
        System.out.println("server name : "+request.getRpcServiceName());
        Object service = serviceProvider.getService(request.getRpcServiceName());
        return invokeTargetMethod(request, service);
    }

    private Object invokeTargetMethod(Request rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException();
        }
        return result;
    }

}
