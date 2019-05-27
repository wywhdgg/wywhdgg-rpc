package com.wywhdgg.framework.demo.consumer;

import com.wywhdgg.framework.client.handler.ClientStubInvocationHandler;
import com.wywhdgg.framework.client.netty.NettyNetClient;
import com.wywhdgg.framework.client.proxy.ClientStubProxyFactory;
import com.wywhdgg.framework.common.protocol.JsonMessageProtocol;
import com.wywhdgg.framework.common.protocol.MessageProtocol;
import com.wywhdgg.framework.discovery.ZookeeperServiceInfoDiscoverer;
import com.wywhdgg.framework.service.DemoService;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:消费者端示例代码
 */
@Slf4j
public class Consumer {
    /*
     * 运行代码依赖zk地址，在app.properties中配置即可
     * 配置项：zk.address=
     */
    public static void main(String[] args) throws Exception {
        // 构建客户端stub代理
        ClientStubProxyFactory clientStubFactory = new ClientStubProxyFactory();
        ClientStubInvocationHandler clientStubInvocationHandler = new ClientStubInvocationHandler(DemoService.class);
        clientStubInvocationHandler.setNetClient(new NettyNetClient());
        clientStubInvocationHandler.setSid(new ZookeeperServiceInfoDiscoverer());
        Map<String, MessageProtocol> supportMessageProtocols = new HashMap<String, MessageProtocol>();
        supportMessageProtocols.put(JsonMessageProtocol.class.getSimpleName(), new JsonMessageProtocol());
        clientStubInvocationHandler.setSupportMessageProtocols(supportMessageProtocols);
        clientStubFactory.setClientStubInvocationHandler(clientStubInvocationHandler);
        // 通过代理工厂获得客户端接口
        DemoService demoService = clientStubFactory.getProxy(DemoService.class);    // 获取远程服务代理
        log.info("=======获得代理接口=============");

        // 执行远程方法
        String message = demoService.sayMessage("world");
        log.info("{}", message); // 显示调用结果

        message = demoService.sayMessage("dog");
        log.info("{}", message); // 显示调用结果
    }
}
