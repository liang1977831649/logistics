package com.logistics.mapper;

import com.logistics.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsMapper {
    List<Goods> getGoodsListByUserId(String name, String id, String userId);

    @Select("select  * from goods where id=#{id}")
    Goods selectGoodsById(String id);

    void insertGoods(Goods goods);

    void updateGoods(Goods goods);

    void deleteGoodsById(String id);

    List<Goods> selectGoodsAll(String name,String id);
}
