package com.logistics.mapper;

import com.logistics.entity.Refrigerate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RefrigerateMapper {
    List<Refrigerate> selectRefrigerateByAreaId(String areaId, String name, String id, Integer status);

    List<Refrigerate> selectRefrigerateByCccId(String cccId);


    Refrigerate selectRefrigerateById(String id);

    Refrigerate selectRefrigerateByGoodsId(String goodsId);

    void updateRefrigerate(Refrigerate refrigerate);

    void insertAddRefrigerate(Refrigerate refrigerate);

    @Select("delete from refrigerate_room where id=#{id}")
    void deleteRefrigerateById(String id);



}
