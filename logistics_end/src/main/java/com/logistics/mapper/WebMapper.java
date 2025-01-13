package com.logistics.mapper;

import com.logistics.entity.Account;
import com.logistics.entity.Admin;
import com.logistics.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WebMapper {
    @Select("select id, name, password, role, area_id from admin where id=#{id}")
    Admin getAdminById(Account account);

    @Select("select * from user where id=#{id} ")
    User getUserById(Account account);
}
