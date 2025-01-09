package com.logistics.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AreaMapper {

    @Select("select areaId from areas where area=#{name}")
    String getAreaCodeByName(String name);
}
