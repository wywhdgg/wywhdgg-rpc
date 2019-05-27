package com.wywhdgg.framework.discovery;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description: 远程服务信息发现接口
 */
public interface ServiceInfoDiscoverer {
    /**
     * 根据服务名获得远程服务信息
     * @param name
     * @return
     */
    public ServiceInfo getServiceInfo(String name);
}
