package com.wywhdgg.framework.common.http;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
public enum Status {
    /**状态码*/
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "ERROR"),
    NOT_FOUND(404, "NOT FOUND");
    private int code;
    private String message;

    private Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
