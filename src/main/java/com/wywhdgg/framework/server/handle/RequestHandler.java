package com.wywhdgg.framework.server.handle;

import com.wywhdgg.framework.common.http.Request;
import com.wywhdgg.framework.common.http.Response;
import com.wywhdgg.framework.common.http.Status;
import com.wywhdgg.framework.common.protocol.MessageProtocol;
import com.wywhdgg.framework.server.register.ServiceObject;
import com.wywhdgg.framework.server.register.ServiceRegister;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
@Slf4j
@Sharable
public class RequestHandler extends ChannelInboundHandlerAdapter {
    private MessageProtocol protocol;

    private ServiceRegister serviceRegister;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel is active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("server receives a message:{}",msg);
        ByteBuf msgBuf = (ByteBuf) msg;
        byte[] req = new byte[msgBuf.readableBytes()];
        msgBuf.readBytes(req);
        byte[] res = handleRequest(req);
        log.info("send response:{}"+msg);
        ByteBuf respBuf = Unpooled.buffer(res.length);
        respBuf.writeBytes(res);
        ctx.write(respBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        log.info("happen exception:{}",cause.getMessage());
        ctx.close();
    }

    public byte[] handleRequest(byte[] data) {
        // 1、解组消息
        Request req = this.protocol.unmarshallingRequest(data);

        // 2、查找服务对象
        ServiceObject so = this.serviceRegister.getServiceObject(req.getServiceName());

        Response rsp = null;

        if (so == null) {
            rsp = new Response(Status.NOT_FOUND);
        } else {
            // 3、反射调用对应的过程方法
            try {
                Method m = so.getInterf().getMethod(req.getMethod(), req.getParameterTypes());
                Object returnValue = m.invoke(so.getObj(), req.getParameters());
                rsp = new Response(Status.SUCCESS);
                rsp.setReturnValue(returnValue);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
                rsp = new Response(Status.ERROR);
                rsp.setException(e);
            }
        }

        // 4、编组响应消息
        return this.protocol.marshallingResponse(rsp);
    }

    public MessageProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(MessageProtocol protocol) {
        this.protocol = protocol;
    }

    public ServiceRegister getServiceRegister() {
        return serviceRegister;
    }

    public void setServiceRegister(ServiceRegister serviceRegister) {
        this.serviceRegister = serviceRegister;
    }

}