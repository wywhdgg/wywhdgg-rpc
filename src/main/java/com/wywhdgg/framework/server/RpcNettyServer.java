package com.wywhdgg.framework.server;

import com.wywhdgg.framework.server.handle.RequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
@Slf4j
public class RpcNettyServer implements Closeable {


    private int port;

    private String protocol;

    private Channel channel;

    private RequestHandler handler;

    public RpcNettyServer(String protocol, int port) {
        this.protocol = protocol;
        this.port = port;
    }

    public void start() {
        // 开启网络服务
        nettyServer(port);
    }

    public void nettyServer(int port) {
        // 创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 指定事件循环组
            bootstrap.group(group)
                // 指定所使用的nio传输channel
                .channel(NioServerSocketChannel.class)
                // 指定本地监听的地址
                .localAddress(new InetSocketAddress(port))
                // 添加一个handler
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(
                            handler
                        );
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 异步的绑定服务器，我们调用sync()方法来执行同步，直到绑定完成
            ChannelFuture future = bootstrap.bind().sync();
            log.info("完成服务端端口绑定与启动");
            channel = future.channel();
            // 获取该Channel的CloseFuture，并且阻塞当前线程直到它完成
            future.channel().closeFuture().sync();
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            // 关闭事件循环组
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        channel.close();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public RequestHandler getHandler() {
        return handler;
    }

    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public void close() throws IOException {
        stop();
    }


}
