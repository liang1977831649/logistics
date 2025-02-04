package com.logistics.mapper;

import com.logistics.entity.Transportation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TransportationMapper {

    @Select("select * from transportation where id=#{id}")
    Transportation  selectTransportationById(String id);


    void insertTransportationByUser(Transportation transportation);

    List<Transportation> selectTransportationList(String id, String goodsName, String userId, String goodsId, Integer status, String carDriId,String areaId);


    @Delete("delete from transportation where id=#{id}")
    void deleteTransportationById(String id);

    Transportation selectTransportationByGoodsId(String goodsId);

    void updateTransportation(Transportation transportation);

    List<Transportation> selectTransportationByTsCarId(String id);

    List<Transportation> selectTransportationListByTsCarId(String tsCarId);
}
