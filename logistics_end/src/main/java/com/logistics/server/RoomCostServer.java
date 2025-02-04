package com.logistics.server;

import com.logistics.entity.PageBean;
import com.logistics.entity.RoomCost;

import java.util.List;

public interface RoomCostServer {
    void addRoomCost(String userId,String deliId,Integer days,Float cost);

    PageBean<RoomCost> selectRoomCostByUserId(Integer pageNum, Integer pageSize,String userId, String deliId, String status,String goodsName);

    void updatePriceRoomCost(RoomCost roomCost);

    RoomCost detailRoomCost(String id);

    List<RoomCost> InComeLastDay();
}
