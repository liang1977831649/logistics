package com.logistics.mapper;


import com.logistics.entity.PageBean;
import com.logistics.entity.RoomCost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RoomCostMapper {

    void insertRoomCost(RoomCost roomCost);


    RoomCost selectRoomCostById(String id);

    List<RoomCost> selectRoomCostList(String userId, String deliId, String status,String goodsName);

    List<RoomCost> selectRoomCostListForAdmin(String userId, String deliId, String status, String goodsName,String areaId);


    void updateRoomCost(RoomCost roomCost);

    RoomCost selectDetailRoomCostById(String id);

    List<RoomCost> selectInComeLastDay(String areaId);
}
