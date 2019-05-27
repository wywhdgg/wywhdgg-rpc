package com.wywhdgg.framework.common.protocol;

import com.alibaba.fastjson.JSON;
import com.wywhdgg.framework.common.http.Request;
import com.wywhdgg.framework.common.http.Response;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:  http协议编组解组器实现
 */
public class HttpMessageProtocol implements MessageProtocol {
    @Override
    public byte[] marshallingRequest(Request req) {
        return new byte[0];
    }

    @Override
    public Request unmarshallingRequest(byte[] data) {
       return  null;
    }

    @Override
    public byte[] marshallingResponse(Response rsp) {

        return new byte[0];
    }

    @Override
    public Response unmarshallingResponse(byte[] data) {
        return JSON.parseObject(data.toString(), Response.class);
    }
}
