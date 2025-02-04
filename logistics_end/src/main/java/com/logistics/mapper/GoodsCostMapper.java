package com.logistics.mapper;

import com.logistics.entity.GoodsCost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsCostMapper {

    @Select("select * from goods_cost where id=#{id}")
    GoodsCost selectGoodsCostById(String id);

    void insertGoodsCost(GoodsCost goodsCost);

    //List<GoodsCost> selectGoodsCostListByUserId(String salesName, String deliId,String goodsName, Integer status, String userId);
    List<GoodsCost> selectGoodsCostListByUserId(String userName, String id,String goodsName, Integer status, String userId,Integer type);

    void updateCostMapper(GoodsCost goodsCost);

    GoodsCost selectGoodsCostByIdForDetail(String id);
}
