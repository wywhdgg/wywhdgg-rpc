package com.wywhdgg.framework.client.handler;

import com.wywhdgg.framework.client.netty.NetClient;
import com.wywhdgg.framework.common.http.Request;
import com.wywhdgg.framework.common.http.Response;
import com.wywhdgg.framework.common.protocol.MessageProtocol;
import com.wywhdgg.framework.discovery.ServiceInfo;
import com.wywhdgg.framework.discovery.ServiceInfoDiscoverer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description: client 客户端存根
 */
@Slf4j
public class ClientStubInvocationHandler implements InvocationHandler {
    private Class<?> interf;
    private ServiceInfoDiscoverer sid;
    private Map<String, MessageProtocol> supportMessageProtocols;
    private NetClient netClient;

    public ClientStubInvocationHandler(Class<?> interf) {
        super();
        this.interf = interf;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1、获得服务信息
        String serviceName = this.interf.getName();
        ServiceInfo sinfo = sid.getServiceInfo(serviceName);

        if (sinfo == null) {
            throw new Exception("远程服务不存在！");
        }

        // 2、构造request对象
        Request req = new Request();
        req.setServiceName(sinfo.getName());
        req.setMethod(method.getName());
        req.setParameterTypes(method.getParameterTypes());
        req.setParameters(args);

        // 3、协议层编组
        // 获得该方法对应的协议
        MessageProtocol protocol = supportMessageProtocols.get(sinfo.getProtocol());
        // 编组请求
        byte[] data = protocol.marshallingRequest(req);

        // 4、调用网络层发送请求
        byte[] repData = netClient.sendRequest(data, sinfo);

        // 5解组响应消息
        Response rsp = protocol.unmarshallingResponse(repData);

        // 6、结果处理
        if (rsp.getException() != null) {
            throw rsp.getException();
        }

        return rsp.getReturnValue();
    }

    public void setSid(ServiceInfoDiscoverer sid) {
        this.sid = sid;
    }

    public void setSupportMessageProtocols(Map<String, MessageProtocol> supportMessageProtocols) {
        this.supportMessageProtocols = supportMessageProtocols;
    }

    public void setNetClient(NetClient netClient) {
        this.netClient = netClient;
    }

    public ServiceInfoDiscoverer getSid() {
        return sid;
    }

    public Map<String, MessageProtocol> getSupportMessageProtocols() {
        return supportMessageProtocols;
    }

    public NetClient getNetClient() {
        return netClient;
    }
}
