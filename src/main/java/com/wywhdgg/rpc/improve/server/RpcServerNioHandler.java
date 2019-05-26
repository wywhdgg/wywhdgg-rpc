package com.wywhdgg.rpc.improve.server;

import com.wywhdgg.rpc.improve.req.RpcRequest;
import com.wywhdgg.rpc.improve.req.RpcResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * RpcServerHandler
 * 服务端请求处理，处理来自网络IO的服务请求，并响应结果给网络IO。
 */
@Slf4j
public class RpcServerNioHandler implements Runnable {
	
	// 客户端网络请求socket，可以从中获得网络请求信息
	private Socket clientSocket;
	
	// 服务端提供处理请求的类集合
	private Map<String, Object> serviceMap;
	
	private SelectionKey registerKey;
	private SocketChannel clientChannel;
	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;
	private int lastReadPos;
	
	public RpcServerNioHandler(final Selector selector, SocketChannel client, Map<String, Object> services){
		this.serviceMap = services;
		clientChannel = client;
		try {
			clientChannel.configureBlocking(false);
			registerKey = clientChannel.register(selector, 0);
			registerKey.interestOps(SelectionKey.OP_READ);
			registerKey.attach(this);
			writeBuffer = ByteBuffer.allocateDirect(1024*2);
			readBuffer = ByteBuffer.allocateDirect(100);
			/*
			writeBuffer.put("Welcome to IoHandler......\r\n".getBytes());
			writeBuffer.flip();
			doWriteData();
			*/
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void doWriteData() {
		writeToChannel();
	}
	
	private void writeToChannel() {
		try {
			// Send
			int wp = clientChannel.write(writeBuffer);
			log.info("IoHandler write "+wp+" bytes data");
			if(writeBuffer.hasRemaining()) {
				registerKey.interestOps(registerKey.interestOps() | SelectionKey.OP_WRITE);
			}else{
				writeBuffer.clear();
				registerKey.interestOps(registerKey.interestOps() &~ SelectionKey.OP_WRITE | SelectionKey.OP_READ);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void doReadData() {
		try {
			clientChannel.read(readBuffer);
			int readEndPos = readBuffer.position();
			String readLine = null;
			for(int i = lastReadPos; i < readEndPos; i++) {
				byte data = readBuffer.get(i);
				if(data == 13){
					byte[] lineBytes = new byte[i - lastReadPos];
					readBuffer.position(lastReadPos);
					readBuffer.get(lineBytes);
					lastReadPos = i;
					// Decode
					readLine = new String(lineBytes);
					break;
				}
			}
			
			if(readLine != null) {
				registerKey.interestOps(registerKey.interestOps() &~ SelectionKey.OP_READ);
				commondLine(readLine);	// Compute 处理业务
			}
			
			if(readBuffer.position() > readBuffer.capacity() / 2) {
				readBuffer.limit(readBuffer.position());
				readBuffer.position(lastReadPos);
				readBuffer.compact();
				lastReadPos = 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Compute
	 */
	private void commondLine(String readLine) {
		String resp = "IoHandler read message: "+readLine+"\r\n";
		System.out.println(resp);
		// Encode
		writeBuffer.put(resp.getBytes());
		writeBuffer.flip();
		writeToChannel();
	}
	
	public void run1() {
		try {
			if(registerKey.isReadable()) {
				System.out.println("Read event");
				doReadData();
			}else if(registerKey.isWritable()) {
				System.out.println("Write event");
				doWriteData();
			}else{
				System.out.println("Other event");
			}
		} catch (Exception e) {
			e.printStackTrace();
			registerKey.cancel();
			
			try {
				clientChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * @param client 客户端socket
	 * @param services 所有服务
	 */
	public RpcServerNioHandler(Socket client, Map<String, Object> services) {
		this.clientSocket = client;
		this.serviceMap = services;
	}
 
 
	/**
	 * 读取网络中客户端请求的信息，找到请求的方法，执行本地方法获得结果，写入网络IO输出中。
	 * 
	 */
	@Override
	public void run() {
		ObjectInputStream oin = null;
		ObjectOutputStream oout = null;
		RpcResponse response = new RpcResponse();
		try {
			// 1. 获取流以待操作
			oin = new ObjectInputStream(clientSocket.getInputStream());
			oout = new ObjectOutputStream(clientSocket.getOutputStream());
			
			// 2. 从网络IO输入流中请求数据，强转参数类型
			Object param = oin.readObject();
			RpcRequest  request = null;
			if(!(param instanceof RpcRequest)){
				response.setError(new Exception("参数错误"));
				oout.writeObject(response);
				oout.flush();
				return;
			}else{
				// 反序列化RpcRequest
				request = (RpcRequest) param;
			}
			
			// 3. 查找并执行服务方法
			Object service = serviceMap.get(request.getClassName());
			Class<?> clazz= service.getClass();
			Method method = clazz.getMethod(request.getMethodName(), request.getParamTypes());
			Object result = method.invoke(service, request.getParams());
			
			System.out.println("返回结果："+response);
			// 4. 返回RPC响应，序列化RpcResponse
			response.setResult(result);
			oout.writeObject(response);
			oout.flush();
			return;
		} catch (Exception e) {
			try {	//异常处理
				if(oout != null){
					response.setError(e);
					oout.writeObject(response);
					oout.flush();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return;
		}finally{
			try {	// 回收资源，关闭流
				if(oin != null) oin.close();
				if(oout != null) oout.close();
				if(clientSocket != null) clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
 
}

