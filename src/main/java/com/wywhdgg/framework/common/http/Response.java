package com.wywhdgg.framework.common.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/***
 *@author dongzhb
 *@date 2019/5/26 11:39
 *@Description:
 *@version 1.0
 */
@Data
@ToString
@AllArgsConstructor
public class Response implements Serializable {
    private static final long serialVersionUID = -5993504059414056034L;
    private  Status status;
    private Map<String,String> headers = new HashMap<>();
    /**响应的内容或结果*/
    private Object returnValue;
    /**异常*/
    private Exception exception;

    public Response() {
    };

    public Response(Status status) {
        this.status=status;
    }
}
