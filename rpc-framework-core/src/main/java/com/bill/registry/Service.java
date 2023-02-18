package com.bill.registry;

import com.bill.loadBanlance.LoadBalance;
import com.bill.remoting.dto.Request;

import java.net.InetSocketAddress;

public interface Service {

    /**
     * 服务注册
     * @param rpcServiceName
     * @param inetSocketAddress
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

    /**
     * 服务发现
     *
     * @param request
     * @return
     */
    InetSocketAddress lookupService(Request request, LoadBalance loadBalance);
}
