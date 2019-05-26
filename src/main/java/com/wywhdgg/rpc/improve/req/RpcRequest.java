package com.wywhdgg.rpc.improve.req;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/***
 *@author lenovo
 *@date 2019/5/26 11:37
 *@Description:
 *@version 1.0
 */
@Data
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -3448579936539657778L;

    // 需要请求的类名
    private String className;

    // 需求请求的方法名
    private String methodName;

    // 请求方法的参数类型
    private Class<?>[] paramTypes;

    // 请求的参数值
    private Object[] params;

}
