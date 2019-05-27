package com.wywhdgg.framework.common.serialize;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description: 序列化信息
 */
public interface Serializer {

    /**
     * 序列化
     * @param obj
     * @return
     */
    Object serialize(Object obj);

    /**
     * 反序列化
     * @param <T>
     * @param obj
     * @param clazz
     * @return
     */
    <T> T deserialize(Object obj, Class<T> clazz);

}
