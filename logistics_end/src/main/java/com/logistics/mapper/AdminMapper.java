package com.logistics.mapper;

import com.logistics.entity.Admin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdminMapper {
    Admin getAdminByIdNoPassword(String id);

    @Update("update admin set password=#{newPwd} where id=#{id}")
    void updateAdminPassword(String newPwd, String id);

    @Select("select * from admin where area_id=#{areaId}")
    Admin selectAdminByAreaId(String areaId);

    @Update("update admin set name=#{name} ,phone=#{phone} where id=#{id}")
    void updateAdmin(Admin admin);

    List<Admin> selectAdminList(String name, String id);

    @Insert("insert into admin(id, name, password, role, phone, area_id) value (#{id},#{name},#{password},0,#{phone},#{areaId})")
    void insertAdmin(Admin admin);

    void updateAdminByManager(Admin admin);
}
