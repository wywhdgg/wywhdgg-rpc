package com.wywhdgg.rpc.improve.service;

import com.wywhdgg.rpc.improve.annotation.Service;

/***
 *@author lenovo
 *@date 2019/5/26 11:49
 *@Description:
 *@version 1.0
 */
@Service(UserService.class)
public class UserServiceImpl implements UserService {
    @Override
    public User getUser() {
        User user = new User();
        user.setName("张三");
        user.setSex("男");
        user.setAge(23);

        return user;
    }

    @Override
    public boolean printInfo(User student) {
        if (student == null) {
            return false;
        }
        return true;
    }
}
