package com.wywhdgg.framework.discovery;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.ToString;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
@Data
@ToString
public class ServiceInfo {
    /** 服务名称 */
    private String name;
    /** 服务提供的协议 */
    private String protocol;
    /** 服务地址信息 */
    private List<String> address;

    public void addAddress(String address) {
        if(this.address == null) {
            this.address = new ArrayList<String>();
        }
        this.address.add(address);
    }
}
