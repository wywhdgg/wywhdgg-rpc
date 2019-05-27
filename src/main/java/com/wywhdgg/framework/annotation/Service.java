package com.wywhdgg.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:一个提供了RPC服务的实现类。
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    /**
     * 注解所有接口类型
     * */
    Class<?> value();

}
