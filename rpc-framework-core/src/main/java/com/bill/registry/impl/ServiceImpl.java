package com.bill.registry.impl;

import com.bill.loadBanlance.LoadBalance;
import com.bill.registry.Service;
import com.bill.registry.util.CuratorUtils;
import com.bill.remoting.dto.Request;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

public class ServiceImpl implements Service {

    /**
     * 服务注册
     *
     * @param rpcServiceName
     * @param inetSocketAddress
     */
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        String path = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName +  inetSocketAddress.toString();
        CuratorUtils.createPersistentNode(zkClient, path);
    }

    /**
     * 服务发现
     *
     * @param request
     * @return
     */
    @Override
    public InetSocketAddress lookupService(Request request, LoadBalance loadBalance) {
        String rpcServiceName = request.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> childrenNodes = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtils.isEmpty(childrenNodes)) {
            //todo 异常处理
            throw new RuntimeException();
        }
        String serviceAddress = loadBalance.selectServiceAddress(childrenNodes, request);
        String host = serviceAddress.split(":")[0];
        int port = Integer.parseInt(serviceAddress.split(":")[1]);
        return new InetSocketAddress(host, port);
    }
}
