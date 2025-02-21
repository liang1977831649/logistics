package com.logistics.mapper;

import com.logistics.entity.Delivery;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeliveryMapper {
    @Select("select * from  delivery where id=#{id}")
    Delivery selectDeliveryById(String id);

    void insertDelivery(Delivery delivery);

    List<Delivery> selectDelivery(String id, String goodsName, Integer status, String userId,String areaId);

    void updateDelivery(Delivery delivery);

    List<Delivery> selectDeliveryByTsCarId(String tsCarId);


    @Delete("delete  from  delivery where id=#{id}")
    void deleteDeliveryById(String id);

    List<Delivery> selectDeliveryListIsOneByGoodsId(String goodsId);

    List<Delivery> selectDeliveryByTsCarIdLimitStatusTow(String tsCarId);

    List<Delivery> selectDeliveryByUserId(String id,String userId,String goodsId,Integer status,String carDriId);
}
