package com.bill.loadBanlance;

import com.bill.remoting.dto.Request;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {
    /**
     * 负载均衡模板方法
     * @param serviceUrlList 服务注册列表
     * @param request        请求
     * @return 目标服务代码
     */
    @Override
    public String selectServiceAddress(List<String> serviceUrlList, Request request) {
        if (CollectionUtils.isEmpty(serviceUrlList)) {
            return null;
        }
        if (serviceUrlList.size() == 1) {
            return serviceUrlList.get(0);
        }
        return select(serviceUrlList, request);
    }

    public abstract String select(List<String> serviceUrlList, Request request);
}
