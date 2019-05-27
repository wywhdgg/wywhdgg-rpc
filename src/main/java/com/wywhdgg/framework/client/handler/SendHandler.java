package com.wywhdgg.framework.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.CountDownLatch;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
public class SendHandler extends ChannelInboundHandlerAdapter {



    private CountDownLatch cdl = null;
    private Object readMsg = null;
    private ChannelHandlerContext ctx;
    private byte[] data;


    public SendHandler(byte[] data) {
        cdl = new CountDownLatch(1);
        this.data = data;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接服务端成功："+ctx);
        this.ctx = ctx;
        ByteBuf reqBuf = Unpooled.buffer(data.length);
        reqBuf.writeBytes(data);
        System.out.println("客户端发送消息："+reqBuf);
        ctx.writeAndFlush(reqBuf);
    }

    public Object rspData() {
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return readMsg;
    }

    public void close() {
        this.ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client read msg: "+msg);
        ByteBuf msgBuf = (ByteBuf) msg;
        byte[] resp = new byte[msgBuf.readableBytes()];
        msgBuf.readBytes(resp);
        readMsg= resp;
        cdl.countDown();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        System.err.println("发生异常："+cause.getMessage());
        ctx.close();
    }

}
