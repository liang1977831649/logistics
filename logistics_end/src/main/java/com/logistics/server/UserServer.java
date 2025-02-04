package com.logistics.server;

import com.logistics.entity.Account;
import com.logistics.entity.PageBean;
import com.logistics.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserServer {
    void insertUserForRegister(Account account);
    PageBean<User> getUserListByAreaId(Integer pageNum, Integer pageSize,String areaId, String name, String id);

    void addUser(User user);

    void updateUser(User user);

    User getUserByIdNoPassword(String id);

    void delete(String id);

    void updatePassword(String oldPwd, String newPwd, String id);

    User detailUser(String id);
}
