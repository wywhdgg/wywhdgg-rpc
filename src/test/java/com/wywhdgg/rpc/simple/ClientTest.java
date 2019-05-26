package com.wywhdgg.rpc.simple;

import com.wywhdgg.rpc.simple.client.RpcClientProxy;
import com.wywhdgg.rpc.simple.service.User;
import com.wywhdgg.rpc.simple.service.UserService;
import org.junit.Test;


/**
 * ClientTest
 * 
 */
public class ClientTest {
	
	@Test
	public void test() {
		// 本地没有接口实现，通过代理获得接口实现实例
		RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9998);
		UserService service = proxy.getProxy(UserService.class);
		
		System.out.println(service.getUser());
		
		User student = new User();
		student.setAge(23);
		student.setName("hashmap");
		student.setSex("男");
		System.out.println(service.printInfo(student));
	}
	
}

