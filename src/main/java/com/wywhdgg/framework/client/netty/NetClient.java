package com.wywhdgg.framework.client.netty;

import com.wywhdgg.framework.discovery.ServiceInfo;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
public interface NetClient {
    byte[] sendRequest(byte[] data, ServiceInfo serviceInfo);
}
