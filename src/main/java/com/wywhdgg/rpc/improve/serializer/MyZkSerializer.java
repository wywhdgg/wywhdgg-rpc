package com.wywhdgg.rpc.improve.serializer;

import java.io.UnsupportedEncodingException;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

/***
 *@author lenovo
 *@date 2019/5/26 16:48
 *@Description:
 *@version 1.0
 */
public class MyZkSerializer implements ZkSerializer {

    String charset = "UTF-8";
    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            throw new ZkMarshallingError(e);
        }
    }

    @Override
    public byte[] serialize(Object obj) throws ZkMarshallingError {
        try {
            return String.valueOf(obj).getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new ZkMarshallingError(e);
        }
    }
}
