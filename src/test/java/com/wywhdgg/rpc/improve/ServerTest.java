package com.wywhdgg.rpc.improve;

import com.wywhdgg.rpc.improve.server.RpcServer;
import org.junit.Test;



/**
 * ServerTest
 * 
 */
public class ServerTest {
	
	@Test
	public void startServer() {
		RpcServer server = new RpcServer();
		server.start(9998, "com.wywhdgg.rpc.improve.service");
	}
}

