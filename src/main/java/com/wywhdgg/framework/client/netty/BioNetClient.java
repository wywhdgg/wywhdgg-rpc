package com.wywhdgg.framework.client.netty;

import com.wywhdgg.framework.discovery.ServiceInfo;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
@Slf4j
public class BioNetClient implements NetClient {
    @Override
    public byte[] sendRequest(byte[] data, ServiceInfo serviceInfo) {
        List<String> addressList = serviceInfo.getAddress();
        int randNum = new Random().nextInt(addressList.size());
        String address = addressList.get(randNum);
        String[] addInfoArray = address.split(":");
        try {
            return startSend(data, addInfoArray[0], Integer.valueOf(addInfoArray[1]));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过网络IO，打开远端服务连接，将请求数据写入网络中，并获得响应结果。
     *
     * @param requestData 将要发送的请求数据
     * @param host 远端服务域名或者ip地址
     * @param port 远端服务端口号
     * @return 服务端响应结果
     * @throws Throwable 抛出的异常
     */
    private byte[] startSend(byte[] requestData, String host, int port) throws Throwable{
        // 打开远端服务连接
        Socket serverSocket = new Socket(host, port);
        InputStream in = null;
        OutputStream out = null;
        try {
            // 1. 服务端输出流，写入请求数据，发送请求数据
            out = serverSocket.getOutputStream();
            out.write(requestData);
            out.flush();

            // 2. 服务端输入流，获取返回数据，转换参数类型
            // 类似于反序列化的过程
            in = serverSocket.getInputStream();
            byte[] res = new byte[1024];
            int readLen = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((readLen = in.read(res)) > 0) {
                baos.write(res, 0, readLen);
            }
            return baos.toByteArray();
        }finally{
            try {	// 清理资源，关闭流
                if(in != null) in.close();
                if(out != null) out.close();
                if(serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
