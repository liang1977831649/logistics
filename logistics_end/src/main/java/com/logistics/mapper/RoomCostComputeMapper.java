package com.logistics.mapper;

import com.logistics.entity.RoomCostCompute;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RoomCostComputeMapper {
    @Select("select * from room_cost_compute where area_id=#{areaId}")
    RoomCostCompute getRoomCostComputeByAreaId(String areaId);

    @Update("update room_cost_compute set weightPrice=#{weightPrice},volumePrice=#{volumePrice},other=#{other} where area_id=#{areaId}")
    void updateRoomCostCompute(RoomCostCompute roomCostCompute);

    @Select("select * from room_cost_compute where area_id=#{areaId}")
    RoomCostCompute selectRoomCostComputeById(String areaId);

    @Insert("insert into room_cost_compute(id, volumePrice, weightPrice, other, area_id) value (null,#{volumePrice},#{weightPrice},#{other},#{areaId})")
    void insertRoomCostCompute(RoomCostCompute roomCostCompute);
}
