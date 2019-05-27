package com.wywhdgg.framework.common.serialize;

import java.io.UnsupportedEncodingException;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
public class MyZkSerializer implements ZkSerializer {

    private  String charset ="UTF-8";

    @Override
    public byte[] serialize(Object o) throws ZkMarshallingError {
        try {
            return  String.valueOf(o).getBytes(charset);
        } catch (UnsupportedEncodingException e) {
          throw  new ZkMarshallingError();
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        try {
            return new String(bytes,charset);
        } catch (UnsupportedEncodingException e) {
            throw  new ZkMarshallingError();
        }
    }
}
