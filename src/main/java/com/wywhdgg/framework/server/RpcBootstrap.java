package com.wywhdgg.framework.server;

import com.wywhdgg.framework.common.protocol.JsonMessageProtocol;
import com.wywhdgg.framework.server.handle.RequestHandler;
import com.wywhdgg.framework.server.register.ServiceObject;
import com.wywhdgg.framework.server.register.ServiceRegister;
import com.wywhdgg.framework.server.register.ZookeeperExportServiceRegister;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
@Slf4j
public class RpcBootstrap {
    private ServiceRegister register = new ZookeeperExportServiceRegister();
    private ServiceLoader loader = new ServiceLoader();
    private String protocol = JsonMessageProtocol.class.getSimpleName();
    private int port = 9082;
    private RpcServer server;

    public void start(String packName) {
        Map<String, Object> services = loader.getService(packName);
        log.info("扫描到实现类={}", services);

        services.forEach((k, v) -> {
            Class<?> interf = null;
            Class<?>[] interfaces = v.getClass().getInterfaces();
            for (Class<?> face : interfaces) {
                if (k.equals(face.getName())) {
                    interf = face;
                }
            }
            ServiceObject so = new ServiceObject(k, interf, v);
            register.register(so, protocol, port);
            log.info("完成类的注册={}", so);
        });

        server = new RpcServer(protocol, port);
        RequestHandler handler = new RequestHandler();
        handler.setProtocol(new JsonMessageProtocol());
        handler.setServiceRegister(register);
        server.setHandler(handler);
        server.start();
    }

    public void close() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
