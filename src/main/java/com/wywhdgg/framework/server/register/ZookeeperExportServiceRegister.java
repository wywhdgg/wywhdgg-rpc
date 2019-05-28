package com.wywhdgg.framework.server.register;

import com.alibaba.fastjson.JSON;
import com.wywhdgg.framework.common.serialize.MyZkSerializer;
import com.wywhdgg.framework.discovery.ServiceInfo;
import com.wywhdgg.framework.util.PropertiesUtils;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
@Slf4j
public class ZookeeperExportServiceRegister extends DefaultServiceRegister implements ServiceRegister {
    private ZkClient client;
    private String centerRootPath="/wywhdggRpc-framework";

    public ZookeeperExportServiceRegister() {
        String addr = PropertiesUtils.getProperties("zk.address");
        client = new ZkClient(addr);
        client.setZkSerializer(new MyZkSerializer());
    }

    public void regist(ServiceInfo serviceResource) {
        String serviceName = serviceResource.getName();
        String uri = JSON.toJSONString(serviceResource);
        try {
            uri = URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String servicePath = centerRootPath + "/" + serviceName + "/service";
        if (!client.exists(servicePath)) {
            client.createPersistent(servicePath, true);
        }
        String uriPath = servicePath + "/" + uri;
        if (client.exists(uriPath)) {
            client.delete(uriPath);
        }
        client.createEphemeral(uriPath);
    }

    /**
     * 加载配置中心中服务资源信息
     */
    public List<ServiceInfo> loadServiceResouces(String serviceName) {
        String servicePath = centerRootPath + "/" + serviceName + "/service";
        List<String> children = client.getChildren(servicePath);
        List<ServiceInfo> resources = new ArrayList<ServiceInfo>();
        for (String ch : children) {
            try {
                String deCh = URLDecoder.decode(ch, "UTF-8");
                ServiceInfo r = JSON.parseObject(deCh, ServiceInfo.class);
                resources.add(r);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return resources;
    }

    @Override
    public void register(ServiceObject so, String protocolName, int port) {
        super.register(so, protocolName, port);
        ServiceInfo soInf = new ServiceInfo();
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            String address = host + ":" + port;
            soInf.addAddress(address);
            soInf.setName(so.getInterf().getName());
            soInf.setProtocol(protocolName);
            regist(soInf);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServiceObject getServiceObject(String name) {
        ServiceObject o = super.getServiceObject(name);
        return o;
    }
}
