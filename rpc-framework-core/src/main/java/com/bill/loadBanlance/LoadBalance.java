package com.bill.loadBanlance;

import com.bill.remoting.dto.Request;

import java.util.List;

public interface LoadBalance {
    /**
     * 负载均衡的实现 
     * @param serviceUrlList 服务注册列表
     * @param request 请求
     * @return 目标服务代码
     */
    String selectServiceAddress(List<String> serviceUrlList, Request request);
}
