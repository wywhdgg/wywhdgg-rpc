package com.wywhdgg.framework.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:  获取配置文件工具类
 */
public class PropertiesUtils {
    private Properties file;

    private static PropertiesUtils instance = new PropertiesUtils();

    private PropertiesUtils() {
        file = new Properties();
        try {
            file.load(PropertiesUtils.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperties(String key) {
        return (String) instance.file.get(key);
    }
}
