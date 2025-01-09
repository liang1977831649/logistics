package com.logistics.mapper;

import com.logistics.entity.Account;
import com.logistics.entity.Admin;
import com.logistics.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WebMapper {
    @Select("select * from admin where id=#{id}")
    public Account getAdminById(Account account);

    @Select("select * from user where id=#{id} ")
    public Account getUserById(Account account);
}
