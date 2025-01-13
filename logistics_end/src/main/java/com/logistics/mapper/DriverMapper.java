package com.logistics.mapper;

import com.logistics.entity.Driver;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DriverMapper {
    List<Driver> selectDriverByAreaId(String areaId, String name, String id,Integer status);


    @Select("select * from driver where id=#{id}")
    Driver selectDriverById(String id);

    void updateDriver(Driver driver);

    void insertDriver(Driver driver);

    @Delete("delete from driver where id=#{id}")
    void deleteById(String id);
}
