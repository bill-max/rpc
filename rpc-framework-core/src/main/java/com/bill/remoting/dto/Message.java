package com.bill.remoting.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Message {

    /**
     * 消息类型
     */
    private byte messageType;

    /**
     * 编码类型
     */
    private byte codec;

    /**
     * 压缩类型
     */
    private byte compress;

    /**
     * 请求序列号
     */
    private int requestId;

    /**
     * 请求体
     */
    private Object data;
}
