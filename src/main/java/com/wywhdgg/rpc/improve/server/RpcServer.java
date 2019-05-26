package com.wywhdgg.rpc.improve.server;

import com.wywhdgg.rpc.improve.annotation.Service;
import com.wywhdgg.rpc.improve.register.RegistCenter;
import com.wywhdgg.rpc.improve.register.ServiceResource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RpcServer
 * 负责导出（export）远程接口
 */
public class RpcServer {
	
	RegistCenter registCenter = new RegistCenter();
	
	Map<String, Object> services;
	
	/**
	 * 启动指定的网络端口号服务，并监听端口上的请求数据。获得请求数据以后将请求信息委派给服务处理器，放入线程池中执行。
	 * @param port 监听端口
	 * @param clazz 服务类所在包名，多个用英文逗号隔开
	 */
	public void start(int port, String clazz) {
		ServerSocket server = null;
		try {
			// 1. 创建服务端指定端口的socket连接
			server = new ServerSocket(port);
			// 2. 获取所有rpc服务类
			services = getService(clazz);
			String host = InetAddress.getLocalHost().getHostAddress();
			exportService(host, port);
			
			// 3. 创建线程池
			Executor executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			while(true){
				// 4. 获取客户端连接
				Socket client = server.accept();
				// 5. 放入线程池中执行
				RpcServerHandler service = new RpcServerHandler(client, services);
				executor.execute(service);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			//关闭监听
			if(server != null)
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public void exportService(String host, int port) {
		services.forEach((k, v)->{
			ServiceResource resource = new ServiceResource();
			resource.setHost(host);
			resource.setPort(port);
			Class<?>[] interfaces = v.getClass().getInterfaces();
			resource.setServiceName(k);
			String methods = "";
			for(Class<?> face : interfaces) {
				if(k.equals(face.getName())) {
					Method[] ms = face.getDeclaredMethods();
					for(Method m : ms) {
						if(!"".equals(methods)) {
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
	 * @param clazz 服务类所在包名，多个用英文逗号隔开
	 * @return
	 */
	public Map<String,Object> getService(String clazz) {
		try {
			Map<String, Object> services = new HashMap<String, Object>();
			// 获取所有服务类
			String[] clazzes = clazz.split(",");
			List<Class<?>> classes = new ArrayList<Class<?>>();
			for(String cl : clazzes){
				List<Class<?>> classList = getClasses(cl);
				classes.addAll(classList);
			}
			// 循环实例化
			for(Class<?> cla : classes) {
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
	 * @param pckgname
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static List<Class<?>> getClasses(String pckgname) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		// 找到指定的包目录
		File directory = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null)
				throw new ClassNotFoundException("无法获取到ClassLoader");
			String path = pckgname.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null)
				throw new ClassNotFoundException("没有这样的资源：" + path);
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
					if(clazz.getAnnotation(Service.class) != null){
						classes.add(clazz);
					}
				}else if(file.isDirectory()){ //如果是目录，递归查找
					List<Class<?>> result = getClasses(pckgname+"."+file.getName());
					if(result != null && result.size() != 0){
						classes.addAll(result);
					}
				}
			}
		} else{
			throw new ClassNotFoundException(pckgname + "不是一个有效的包名");
		}
		return classes;
	}
}
