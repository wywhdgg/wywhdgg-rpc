package com.wywhdgg.framework.server.register;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
@Slf4j
public class DefaultServiceRegister implements ServiceRegister {
    /** 存放服务实例 */
    private Map<String, ServiceObject> serviceMap = new HashMap<>();

    @Override
    public void register(ServiceObject so, String protocolName, int port) {
        if (so == null) {
            throw new IllegalArgumentException("register params is empty");
        }
        serviceMap.put(so.getName(), so);
    }

    @Override
    public ServiceObject getServiceObject(String name) {
        return this.serviceMap.get(name);
    }
}
