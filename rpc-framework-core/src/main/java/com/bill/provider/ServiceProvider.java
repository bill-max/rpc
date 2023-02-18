package com.bill.provider;

import com.bill.config.ServiceConfig;

public interface ServiceProvider {
    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void addService(ServiceConfig rpcServiceConfig);

    /**
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void publishService(ServiceConfig rpcServiceConfig);

}
