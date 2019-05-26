package com.wywhdgg.rpc.improve;

import com.wywhdgg.rpc.improve.client.RpcNioServer;
import java.io.IOException;
import org.junit.Test;

/**
 * ServerTest
 */
public class ServerNioTest {
    @Test
    public void startServer() throws IOException {
        RpcNioServer server = new RpcNioServer(9998,  "com.wywhdgg.rpc.improve.service");
        server.start();
    }
}

