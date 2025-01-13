package com.logistics.mapper;

import com.logistics.entity.Account;
import com.logistics.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    void insertUserFroRegister(User user);

    List<User> selectUserByAreaId(String areaId,String name,String id);

    void insertUserForAdd(User user);

    void update(User user);

    User getUserByIdNoPassword(String id);

    void deleteUserById(String id);

    @Update("update user set password=#{newPwd} where id=#{id}")
    void updateUserPassword(String newPwd, String id);
}
