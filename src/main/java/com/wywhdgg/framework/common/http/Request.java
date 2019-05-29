package com.wywhdgg.framework.common.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.ToString;

/***
 *@author dzb
 *@date 2019/5/27
 *@Description:
 *@version 1.0
 */
@Data
@ToString
public class Request implements Serializable {
    private static final long serialVersionUID = -3448579936539657778L;
    /** 请求的类名 */
    private String serviceName;
    /** 请求的方法名 */
    private String method;
    private Map<String, String> headers = new HashMap<String, String>();
    /** 请求方法的参数类型 */
    private Class<?>[] parameterTypes;
    /** 请求的参数值 */
    private Object[] parameters;
}
