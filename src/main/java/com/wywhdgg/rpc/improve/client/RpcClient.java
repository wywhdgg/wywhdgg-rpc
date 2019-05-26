package com.wywhdgg.rpc.improve.client;

import com.wywhdgg.rpc.improve.register.RegistCenter;
import com.wywhdgg.rpc.improve.register.ServiceResource;
import com.wywhdgg.rpc.improve.req.RpcRequest;
import com.wywhdgg.rpc.improve.req.RpcResponse;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Random;

/**
 * RpcClient
 * Rpc客户端，代表业务代码作为客户端，往远端服务发起请求。
 */
public class RpcClient {
	RegistCenter registCenter = new RegistCenter();
	public static final RpcClient INSTANCE = new RpcClient();
	
	public List<ServiceResource> importService(String serviceName) {
		List<ServiceResource> resouces = registCenter.loadServiceResouces(serviceName);
		return resouces;
	}

	/**
	 * 地址存在zk
	 * */
	public Object invoke(RpcRequest request) throws Throwable {
		List<ServiceResource> resources = RpcClient.INSTANCE.importService(request.getClassName());
		int index = new Random().nextInt(resources.size());
		ServiceResource resource = resources.get(index);
		if(resource.getMethods().indexOf(request.getMethodName()) < 0) {
			throw new IllegalAccessException("服务方 ["+request.getClassName()+"] 没有提供"+request.getMethodName()+"方法");
		}
		return invoke(request, resource.getHost(), resource.getPort());
	}
	
	/**
	 * 通过网络IO，打开远端服务连接，将请求数据写入网络中，并获得响应结果。
	 * 
	 * @param request 将要发送的请求数据
	 * @param host 远端服务域名或者ip地址
	 * @param port 远端服务端口号
	 * @return 服务端响应结果
	 * @throws Throwable 抛出的异常
	 */
	public Object invoke(RpcRequest request, String host, int port) throws Throwable {
		// 打开远端服务连接
		Socket server = new Socket(host, port);
		
		ObjectInputStream oin = null;
		ObjectOutputStream oout = null;
		
		try {
			// 1. 服务端输出流，写入请求数据，发送请求数据
			oout = new ObjectOutputStream(server.getOutputStream());
			oout.writeObject(request);
			oout.flush();
			
			// 2. 服务端输入流，获取返回数据，转换参数类型
			// 类似于反序列化的过程
			oin = new ObjectInputStream(server.getInputStream());
			Object res = oin.readObject();
			RpcResponse response = null;
			if(!(res instanceof RpcResponse)){
				throw new InvalidClassException("返回参数不正确，应当为："+RpcResponse.class+" 类型");
			}else{
				response = (RpcResponse) res;
			}
			
			// 3. 返回服务端响应结果
			if(response.getError() != null){ // 服务器产生异常
				throw response.getError();
			}
			return response.getResult();
		}finally{
			try {	// 清理资源，关闭流
				if(oin != null) oin.close();
				if(oout != null) oout.close();
				if(server != null) server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

