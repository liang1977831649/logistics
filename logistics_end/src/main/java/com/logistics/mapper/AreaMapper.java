package com.logistics.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AreaMapper {

    @Select("select areaId from areas where area=#{name}")
    String getAreaCodeByName(String name);

    @Select("select area from  areas where areaid=#{areaId}")
    String getAreaNameByAreaCode(String code);

    @Select("select cityid from areas where areaid=#{id}")
    public String getCityIdByAreaId(String id);

    @Select("select provinceid from cities where cityid=#{id}")
    public String getProvinceIdByCityId(String id);

}
