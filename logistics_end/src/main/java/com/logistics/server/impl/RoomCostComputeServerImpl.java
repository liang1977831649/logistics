package com.logistics.server.impl;

import com.logistics.entity.RoomCostCompute;
import com.logistics.mapper.RoomCostComputeMapper;
import com.logistics.server.RoomCostComputeServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class RoomCostComputeServerImpl implements RoomCostComputeServer {
    @Autowired
    private RoomCostComputeMapper roomCostComputeMapper;

    @Override
    public void updateRoomCostCompute(RoomCostCompute roomCostCompute) {
        String areaId=(String)((HashMap<String,Object>)ThreadLocalUtils.get()).get("areaId");
        roomCostCompute.setAreaId(areaId);
        //先看看存不存在，如果不存在，就添加返回
        RoomCostCompute roomCostComputeById = roomCostComputeMapper.selectRoomCostComputeById(areaId);
        if(roomCostComputeById==null){
            roomCostComputeMapper.insertRoomCostCompute(roomCostCompute);
            return;
        }
        roomCostComputeMapper.updateRoomCostCompute(roomCostCompute);
    }

    @Override
    public RoomCostCompute getRoomCostCompute() {
        String areaId=(String)((HashMap<String,Object>)ThreadLocalUtils.get()).get("areaId");
        RoomCostCompute roomCostComputeByAreaId = roomCostComputeMapper.getRoomCostComputeByAreaId(areaId);
        return roomCostComputeByAreaId;
    }

}
