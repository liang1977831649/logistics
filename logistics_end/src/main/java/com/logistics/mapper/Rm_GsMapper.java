package com.logistics.mapper;

import com.logistics.entity.Rm_Gs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface Rm_GsMapper {
    @Select("select * from room_goods where goods_id=#{id}")
    Rm_Gs selectRm_GsByGoodsId(String id);

    @Update("update room_goods set rm_id=#{rmId} where goods_id=#{gdId}")
    void updateRmIdByGoodsId(Rm_Gs rmGs);
}
