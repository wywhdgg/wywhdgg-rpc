package com.wywhdgg.framework.client.proxy;

import com.wywhdgg.framework.client.handler.ClientStubInvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:客户端存根代理工厂
 */
@Slf4j
public class ClientStubProxyFactory {
    private Map<Class<?>, Object> objectCache = new HashMap<>();
    private  ClientStubInvocationHandler clientStubInvocationHandler;

    /**
     *
     *
     * @param <T>
     * @param interf
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> interf) {
        T obj = (T) this.objectCache.get(interf);
        if (obj == null) {
            obj = (T) Proxy.newProxyInstance(interf.getClassLoader(), new Class<?>[] {interf}, new ClientStubInvocationHandler(interf));
            this.objectCache.put(interf, obj);
        }
        return obj;
    }

    public void setClientStubInvocationHandler(ClientStubInvocationHandler clientStubInvocationHandler) {
        this.clientStubInvocationHandler = clientStubInvocationHandler;
    }
}
