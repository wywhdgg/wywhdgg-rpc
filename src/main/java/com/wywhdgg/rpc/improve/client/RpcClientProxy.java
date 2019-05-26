package com.wywhdgg.rpc.improve.client;

import com.wywhdgg.rpc.improve.req.RpcRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import lombok.extern.slf4j.Slf4j;

/**
 * RpcClientProxy
 * 客户端代理服务，客户端往服务端发起的调用将通过客户端代理来发起
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler{
	
	/**
	 * 生成业务接口的代理对象，代理对象做的事情，在invoke方法中。
	 * @param clazz 代理类型（接口）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> clazz){
		// clazz 不是接口不能使用JDK动态代理
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{ clazz }, RpcClientProxy.this);
	}
	
	/**
	 * 动态代理做的事情，接口的实现不在本地，在网络中的其他进程中，我们通过实现了Rpc客户端的对象来发起远程服务的调用。
	 */
	@Override
	public Object invoke(Object obj, Method method, Object[] params) throws Throwable {
		
		// 调用前
		log.info("before invoke doSomething......");
		
		// 封装参数，类似于序列化的过程
		RpcRequest request = new RpcRequest();
		request.setClassName(method.getDeclaringClass().getName());
		request.setMethodName(method.getName());
		request.setParamTypes(method.getParameterTypes());
		request.setParams(params);
		
		// 链接服务器调用服务
		Object rst = RpcClient.INSTANCE.invoke(request);
		
		// 调用后
		log.info("after invoke doSomething......");
		return rst;
	}
	
}

