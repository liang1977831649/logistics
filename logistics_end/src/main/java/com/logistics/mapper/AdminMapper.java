package com.logistics.mapper;

import com.logistics.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminMapper {
    Admin getAdminByIdNoPassword(String  id );

    @Update("update admin set password=#{newPwd} where id=#{id}")
    void updateAdminPassword(String newPwd, String id);
}
