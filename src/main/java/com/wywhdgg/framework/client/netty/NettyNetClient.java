package com.wywhdgg.framework.client.netty;

import com.wywhdgg.framework.client.handler.SendHandler;
import com.wywhdgg.framework.discovery.ServiceInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
@Slf4j
public class NettyNetClient implements NetClient {
    Lock lock = new ReentrantLock();
    @Override
    public byte[] sendRequest(byte[] data, ServiceInfo serviceInfo) {
        try {
            List<String> addressList = serviceInfo.getAddress();
            int randNum = new Random().nextInt(addressList.size());
            String address = addressList.get(randNum);
            String[] addInfoArray = address.split(":");
            lock.lock();
            try {
                SendHandler sendHandler = new SendHandler(data);
                new Thread(()->{
                    try {
                        System.out.println(Thread.currentThread().getName()+" 启动netty");
                        new nettyConnecter().connect(addInfoArray[0], Integer.valueOf(addInfoArray[1]), sendHandler);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                byte[] respData = (byte[]) sendHandler.rspData();
                sendHandler.close();
                return respData;
            } finally {
                lock.unlock();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class nettyConnecter {

        public void connect(String host, int port, SendHandler handler) throws Exception {
            // 配置客户端
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                //EchoClientHandler handler = new EchoClientHandler();
                b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(handler);
                        }
                    });

                // 启动客户端连接
                ChannelFuture f = b.connect(host, port).sync();
                // 等待客户端连接关闭
                f.channel().closeFuture().sync();
                System.out.println(Thread.currentThread().getName()+" netty 通道即将关闭");
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName()+" netty释放资源");
                // 释放线程组资源
                group.shutdownGracefully();
            }
        }
    }
}
