package com.bill.remoting.transport.netty.client;

import com.bill.remoting.dto.Response;

import java.net.Inet4Address;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Process {
    private final static Map<String, CompletableFuture<Response<Object>>> PROCESS_MAP = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<Response<Object>> future) {
        PROCESS_MAP.put(requestId, future);
    }

    public void process(Response<Object> response) {
        CompletableFuture<Response<Object>> f = PROCESS_MAP.remove(response.getRequestId());
        if (f != null) {
            //处理request
            f.complete(response);
        } else {
            throw new RuntimeException();
        }
    }

}
