package com.wywhdgg.framework.client.proxy;

import com.wywhdgg.framework.client.handler.ClientStubInvocationHandler;
import com.wywhdgg.framework.client.netty.NettyNetClient;
import com.wywhdgg.framework.common.protocol.JsonMessageProtocol;
import com.wywhdgg.framework.common.protocol.MessageProtocol;
import com.wywhdgg.framework.discovery.ZookeeperServiceInfoDiscoverer;
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
            obj = (T) Proxy.newProxyInstance(interf.getClassLoader(), new Class<?>[] {interf}, getClientStubInvocationHandler(interf));
            this.objectCache.put(interf, obj);
        }
        return obj;
    }

    public ClientStubInvocationHandler getClientStubInvocationHandler(Class<?> interf) {
        ClientStubInvocationHandler clientStubInvocationHandler = new ClientStubInvocationHandler(interf);
        clientStubInvocationHandler.setNetClient(new NettyNetClient());
        clientStubInvocationHandler.setSid(new ZookeeperServiceInfoDiscoverer());
        Map<String, MessageProtocol> supportMessageProtocols = new HashMap<String, MessageProtocol>();
        supportMessageProtocols.put(JsonMessageProtocol.class.getSimpleName(), new JsonMessageProtocol());
        clientStubInvocationHandler.setSupportMessageProtocols(supportMessageProtocols);
        return clientStubInvocationHandler;
    }
}
