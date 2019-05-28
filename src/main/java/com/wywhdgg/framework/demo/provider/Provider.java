package com.wywhdgg.framework.demo.provider;

import com.wywhdgg.framework.server.RpcBootstrap;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
public class Provider {

    /*
     * 运行代码依赖zk地址，在app.properties中配置即可
     * 配置项：zk.address=
     */
    public static void main(String[] args) throws Exception {
        RpcBootstrap bootstrap = new RpcBootstrap();
        bootstrap.start("com.wywhdgg.framework.service.impl");
        System.in.read(); // 按任意键退出
    }
}
