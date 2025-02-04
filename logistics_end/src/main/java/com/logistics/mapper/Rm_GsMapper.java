package com.logistics.mapper;

import com.logistics.entity.Rm_Gs;
import org.apache.ibatis.annotations.*;

@Mapper
public interface Rm_GsMapper {
    @Select("select * from room_goods where goods_id=#{id}")
    Rm_Gs selectRm_GsByGoodsId(String id);

    @Update("update room_goods set rm_id=#{rmId} where goods_id=#{gdId}")
    void updateRmIdByGoodsId(Rm_Gs rmGs);

    @Insert("insert into room_goods(id, rm_id, goods_id,status, createTime) value(null,#{rmId},#{gdId},1,now()) ")
    void addRmGs(Rm_Gs rmGs);

    @Delete("delete from room_goods where goods_id=#{id}")
    void deleteByGoodsId(String id);

    @Update("update room_goods set status=#{status} where goods_id=#{goodsId}")
    void updateRm_GsStatusByGoodsId(Integer status,String goodsId);
}
