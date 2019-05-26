package com.wywhdgg.rpc.simple;

import com.wywhdgg.rpc.simple.server.RpcServer;
import java.lang.reflect.Method;

import org.junit.Test;


/**
 * ServerTest
 * 
 */
public class ServerTest {
	
	@Test
	public void startServer() {
		RpcServer server = new RpcServer();
		server.start(9998, "com.wywhdgg.rpc.simple.service");
	}
	
	public static void main(String[] args) {
	}
}

