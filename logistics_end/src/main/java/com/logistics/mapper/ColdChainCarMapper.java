package com.logistics.mapper;

import com.logistics.entity.ColdChainCar;
import com.logistics.entity.Driver;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ColdChainCarMapper {

    List<ColdChainCar> selectColdChainCarByAreaId(String areaId, String name, String id, Integer status);

    @Select("select * from cold_chain_car where id=#{id}")
    ColdChainCar selectColdChainCarById(String id);

    void updateCar(ColdChainCar coldChainCar);

    void addColdChainCa(ColdChainCar coldChainCar);

    @Delete("delete from cold_chain_car where id=#{id}")
    void deleteColdChainCarById(String id);
}
