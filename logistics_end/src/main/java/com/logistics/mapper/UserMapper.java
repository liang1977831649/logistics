package com.logistics.mapper;

import com.logistics.entity.Account;
import com.logistics.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void insertUserFroRegister(User user);

}
