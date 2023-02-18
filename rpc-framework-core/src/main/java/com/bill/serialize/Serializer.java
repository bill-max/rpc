package com.bill.serialize;

public interface Serializer {
    /**
     * 序列化
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
