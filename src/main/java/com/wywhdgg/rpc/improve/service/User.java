package com.wywhdgg.rpc.improve.service;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/***
 *@author lenovo
 *@date 2019/5/26 11:47
 *@Description:
 *@version 1.0
 */
@Data
@ToString
public class User implements Serializable {
    private String name;
    private int age;
    private String sex;
}
