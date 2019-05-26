package com.wywhdgg.rpc.improve.client;

import com.wywhdgg.rpc.improve.annotation.Service;
import com.wywhdgg.rpc.improve.register.RegistCenter;
import com.wywhdgg.rpc.improve.register.ServiceResource;
import com.wywhdgg.rpc.improve.server.RpcServerNioHandler;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RpcServer 负责导出（export）远程接口
 */
public class RpcNioServer extends Thread {
    RegistCenter registCenter = new RegistCenter();
    Executor executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private Selector selector;
    private ServerSocketChannel socketChannel;
    Map<String, Object> services;

    public RpcNioServer(int port, String clazz) throws IOException {
        // 初始化
        selector = Selector.open();
        // 绑定监听服务通道
        socketChannel = ServerSocketChannel.open();
        socketChannel.socket().bind(new InetSocketAddress(port));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        services = getService(clazz);
        String host = InetAddress.getLocalHost().getHostAddress();
        exportService(host, port);
    }

    /**
     * 启动指定的网络端口号服务，并监听端口上的请求数据。获得请求数据以后将请求信息委派给服务处理器，放入线程池中执行。
     */
    @Override
    public void run() {
        while (true) {    // Event Loop
            try {
                //有点问题
                int selectNums = selector.select(500);
                System.out.println("selectNums: "+selectNums);
                if (selectNums <= 0) {
                    continue;
                }

                // selector提供了同时关注多个事件的能力
                Set<SelectionKey> selectedKeySet = selector.selectedKeys();
                //System.out.println("selectNums: "+selectedKeySet);
                for (SelectionKey currentKey : selectedKeySet) {
                    // 派遣处理网络连接请求的事件
                    if (currentKey.isAcceptable()) {
                        //System.out.println("Accept event");
                        new Acceptor(currentKey).doAccept(services);
                    }
                    // 派遣处理读写数据的事件，以及任务处理
                    else {
                        System.out.println("io");
                        executor.execute(((RpcServerNioHandler) currentKey.attachment()));
                    }
                }
                selectedKeySet.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 接受客户端连接
     */
    class Acceptor {
        private SelectionKey currentKey;

        public Acceptor(SelectionKey key) {
            this.currentKey = key;
        }

        public void doAccept(Map<String, Object> services) {
            try {
                ServerSocketChannel serverChannel = (ServerSocketChannel) currentKey.channel();
                // 接受客户端连接
                SocketChannel clientChannel = serverChannel.accept();
                // IO读写处理器
                new RpcServerNioHandler(selector, clientChannel, services);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportService(String host, int port) {
        services.forEach((k, v) -> {
            ServiceResource resource = new ServiceResource();
            resource.setHost(host);
            resource.setPort(port);
            Class<?>[] interfaces = v.getClass().getInterfaces();
            resource.setServiceName(k);
            String methods = "";
            for (Class<?> face : interfaces) {
                if (k.equals(face.getName())) {
                    Method[] ms = face.getDeclaredMethods();
                    for (Method m : ms) {
                        if (!"".equals(methods)) {
                            methods += ",";
                        }
                        methods += m.getName();
                    }
                }
            }
            resource.setMethods(methods);
            registCenter.regist(resource);
        });
    }

    /**
     * 实例化所有rpc服务类，也可用于暴露服务信息到注册中心。
     *
     * @param clazz 服务类所在包名，多个用英文逗号隔开
     */
    public Map<String, Object> getService(String clazz) {
        try {
            Map<String, Object> services = new HashMap<String, Object>();
            // 获取所有服务类
            String[] clazzes = clazz.split(",");
            List<Class<?>> classes = new ArrayList<Class<?>>();
            for (String cl : clazzes) {
                List<Class<?>> classList = getClasses(cl);
                classes.addAll(classList);
            }
            // 循环实例化
            for (Class<?> cla : classes) {
                Object obj = cla.newInstance();
                services.put(cla.getAnnotation(Service.class).value().getName(), obj);
            }
            return services;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取包下所有有@Sercive注解的类
     *
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> getClasses(String pckgname) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        // 找到指定的包目录
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("无法获取到ClassLoader");
            }
            String path = pckgname.replace('.', '/');
            URL resource = cld.getResource(path);
            if (resource == null) {
                throw new ClassNotFoundException("没有这样的资源：" + path);
            }
            directory = new File(resource.getFile());
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " (" + directory + ") 不是一个有效的资源");
        }
        if (directory.exists()) {
            // 获取包目录下的所有文件
            String[] files = directory.list();
            File[] fileList = directory.listFiles();
            // 获取包目录下的所有文件
            for (int i = 0; fileList != null && i < fileList.length; i++) {
                File file = fileList[i];
                //判断是否是Class文件
                if (file.isFile() && file.getName().endsWith(".class")) {
                    Class<?> clazz = Class.forName(pckgname + '.' + files[i].substring(0, files[i].length() - 6));
                    if (clazz.getAnnotation(Service.class) != null) {
                        classes.add(clazz);
                    }
                } else if (file.isDirectory()) { //如果是目录，递归查找
                    List<Class<?>> result = getClasses(pckgname + "." + file.getName());
                    if (result != null && result.size() != 0) {
                        classes.addAll(result);
                    }
                }
            }
        } else {
            throw new ClassNotFoundException(pckgname + "不是一个有效的包名");
        }
        return classes;
    }
}
