package com.wywhdgg.rpc.simple.client;

import com.wywhdgg.rpc.simple.req.RpcRequest;
import com.wywhdgg.rpc.simple.req.RpcResponse;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/***
 *@author lenovo
 *@date 2019/5/26 11:31
 *@Description:
 *@version 1.0
 */
public class RpcClient {
    /**
     * 通过网络IO，打开远端服务连接，将请求数据写入网络中，并获得响应结果。
     *
     * @param request 将要发送的请求数据
     * @param host 远端服务域名或者ip地址
     * @param port 远端服务端口号
     * @return 服务端响应结果
     * @throws Throwable 抛出的异常
     */
    public Object start(RpcRequest request, String host, int port) throws Throwable {
        // 打开远端服务连接
        Socket server = new Socket(host, port);

        ObjectInputStream oin = null;
        ObjectOutputStream oout = null;

        try {
            // 1. 服务端输出流，写入请求数据，发送请求数据
            oout = new ObjectOutputStream(server.getOutputStream());
            oout.writeObject(request);
            oout.flush();

            // 2. 服务端输入流，获取返回数据，转换参数类型
            // 类似于反序列化的过程
            oin = new ObjectInputStream(server.getInputStream());
            Object res = oin.readObject();
            RpcResponse response = null;
            if (!(res instanceof RpcResponse)) {
                throw new InvalidClassException("返回参数不正确，应当为：" + RpcResponse.class + " 类型");
            } else {
                response = (RpcResponse) res;
            }

            // 3. 返回服务端响应结果
            if (response.getError() != null) { // 服务器产生异常
                throw response.getError();
            }
            return response.getResult();
        } finally {
            try {    // 清理资源，关闭流
                if (oin != null) {
                    oin.close();
                }
                if (oout != null) {
                    oout.close();
                }
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
