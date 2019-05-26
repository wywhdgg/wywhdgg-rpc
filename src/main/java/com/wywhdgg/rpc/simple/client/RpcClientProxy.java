package com.wywhdgg.rpc.simple.client;

import com.wywhdgg.rpc.simple.req.RpcRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import lombok.extern.slf4j.Slf4j;

/***
 *@author lenovo
 *@date 2019/5/26 11:31
 *@Description:
 *@version 1.0
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
    private String name;
    private int host;

    public RpcClientProxy(String name,int host) {
        this.host = host;
        this.name = name;
    }

    /**
     * 生成业务接口的代理对象，代理对象做的事情，在invoke方法中。
     *
     * @param clazz 代理类型（接口）
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        // clazz 不是接口不能使用JDK动态代理
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] {clazz}, RpcClientProxy.this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        log.info("before invoke doSomething ......");
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParamTypes(method.getParameterTypes());
        rpcRequest.setParams(params);
        RpcClient rpcClient = new RpcClient();
        Object rst = rpcClient.start(rpcRequest, name, host);
        log.info("after invoke doSomething......");

        return rst;
    }
}
