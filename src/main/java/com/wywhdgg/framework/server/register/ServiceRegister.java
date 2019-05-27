package com.wywhdgg.framework.server.register;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description: 服务器注册
 */
public interface ServiceRegister {
    /**
     * 注册服务
     * @param so
     */
    void register(ServiceObject so, String protocolName, int port);

    /**
     * 获取服务对象
     * @param name
     * @return
     */
    ServiceObject getServiceObject(String name);
}
