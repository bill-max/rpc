package com.bill.loadBanlance.impl;

import com.bill.loadBanlance.AbstractLoadBalance;
import com.bill.remoting.dto.Request;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡方式：随机
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    public String select(List<String> serviceUrlList, Request request) {
        Random random = new Random();
        return serviceUrlList.get(random.nextInt(serviceUrlList.size()));
    }
}
