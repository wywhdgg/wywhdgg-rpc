package com.wywhdgg.rpc.improve.req;

import java.io.Serializable;
import lombok.Data;

/***
 *@author lenovo
 *@date 2019/5/26 11:39
 *@Description:
 *@version 1.0
 */
@Data
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = -5993504059414056034L;
    // 可能抛出的异常
    private Throwable error;
    // 响应的内容或结果
    private Object result;
}
