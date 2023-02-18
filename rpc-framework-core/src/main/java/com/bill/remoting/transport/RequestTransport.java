package com.bill.remoting.transport;

import com.bill.remoting.dto.Request;


public interface RequestTransport {

    /**
     * 消息传输顶层接口
     * @param request 请求
     * @return 服务端返回消息
     */
    Object sendRequest(Request request);
}
