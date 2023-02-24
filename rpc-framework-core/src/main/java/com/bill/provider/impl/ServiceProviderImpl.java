package com.bill.provider.impl;

import com.bill.config.ServiceConfig;
import com.bill.provider.ServiceProvider;
import com.bill.registry.Service;
import com.bill.registry.impl.ServiceImpl;
import com.bill.remoting.transport.socket.SocketServer;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.spi.ServiceRegistry;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServiceProviderImpl implements ServiceProvider {
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();
    private final Service service = new ServiceImpl();


    public ServiceProviderImpl() {
    }

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    @Override
    public void addService(ServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        System.out.println("service : " + rpcServiceConfig.getService().toString());
        log.info("Add service: {} and interfaces:{}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    /**
     * @param rpcServiceName rpc service name
     * @return service object
     */
    @Override
    public Object getService(String rpcServiceName) {
        System.out.println("rpc name:" + rpcServiceName);
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RuntimeException();
        }
        return service;
    }

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    @Override
    public void publishService(ServiceConfig rpcServiceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcServiceConfig);
            service.registerService(rpcServiceConfig.getRpcServiceName(), new InetSocketAddress(host, SocketServer.PORT));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }
}
