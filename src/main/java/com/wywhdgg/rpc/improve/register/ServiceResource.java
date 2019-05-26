package com.wywhdgg.rpc.improve.register;

import lombok.Data;

/**
 * ServiceUri
 */
@Data
public class ServiceResource {
    private String host;
    private int port;
    private String serviceName;
    private String methods;
}

