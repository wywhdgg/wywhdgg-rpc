package com.wywhdgg.framework.common.protocol;

import com.wywhdgg.framework.common.http.Request;
import com.wywhdgg.framework.common.http.Response;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:  通讯协议接口
 */
public interface MessageProtocol {
    /**
     * 编组请求消息
     * @param req
     * @return
     */
    byte[] marshallingRequest(Request req);

    /**
     * 解编组请求消息
     * @param data
     * @return
     */
    Request unmarshallingRequest(byte[] data);

    /**
     * 编组响应消息
     * @param rsp
     * @return
     */
    byte[] marshallingResponse(Response rsp);

    /**
     * 解编组响应消息
     * @param data
     * @return
     */
    Response unmarshallingResponse(byte[] data);
}
