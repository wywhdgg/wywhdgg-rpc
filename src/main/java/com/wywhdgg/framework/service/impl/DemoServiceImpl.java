package com.wywhdgg.framework.service.impl;

import com.wywhdgg.framework.annotation.Service;
import com.wywhdgg.framework.service.DemoService;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:
 */
@Service(DemoService.class)
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayMessage(String message) {
        StringBuffer sb = new StringBuffer("测试用例，返回的信息:");
        sb.append(message);
        return sb.toString();
    }
}
