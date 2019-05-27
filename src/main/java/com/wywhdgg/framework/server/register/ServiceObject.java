package com.wywhdgg.framework.server.register;

import lombok.Data;
import lombok.ToString;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
@Data
@ToString
public class ServiceObject {
    /** 服务名称 */
    private String name;
    /** 接口 */
    private Class<?> interf;
    /** 对象实例 */
    private Object obj;

    public ServiceObject(String name, Class<?> interf, Object obj) {
        super();
        this.name = name;
        this.interf = interf;
        this.obj = obj;
    }
}
