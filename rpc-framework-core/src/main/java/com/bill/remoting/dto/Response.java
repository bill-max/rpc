package com.bill.remoting.dto;

import com.bill.enums.ResponseCodeEnum;
import lombok.*;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 715745410605631233L;
    /**
     * 请求序列号
     */
    private String requestId;
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 返回体
     */
    private T data;

    /**
     * @param <T>  对于static方法，必须声明<T>,即声明该方法为泛型方法
     */
    public static <T> Response<T> success(T data, String requestId) {
        Response<T> response = new Response<>();
        response.setCode(ResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(ResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (data != null) {
            response.setData(data);
        }
        return response;
    }

    public static <T> Response<T> fail(ResponseCodeEnum responseCodeEnum) {
        Response<T> response = new Response<>();
        response.setCode(responseCodeEnum.getCode());
        response.setMessage(responseCodeEnum.getMessage());
        return response;
    }
}
