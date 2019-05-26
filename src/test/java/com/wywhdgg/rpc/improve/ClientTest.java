package com.wywhdgg.rpc.improve;

import com.wywhdgg.rpc.improve.client.RpcClientProxy;
import com.wywhdgg.rpc.improve.service.User;
import com.wywhdgg.rpc.improve.service.UserService;
import org.junit.Test;



/**
 * ClientTest
 * 
 */
public class ClientTest {
	
	@Test
	public void test() {
		// 本地没有接口实现，通过代理获得接口实现实例
		RpcClientProxy proxy = new RpcClientProxy();
		UserService service = proxy.getProxy(UserService.class);

		System.out.println(service.getUser());

		User student = new User();
		student.setAge(23);
		student.setName("hashmap");
		student.setSex("男");
		System.out.println(service.printInfo(student));
	}
	
}

