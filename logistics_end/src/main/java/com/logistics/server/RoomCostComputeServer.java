package com.logistics.server;

import com.logistics.entity.RoomCostCompute;

public interface RoomCostComputeServer {
    void updateRoomCostCompute(RoomCostCompute roomCostCompute);

    RoomCostCompute getRoomCostCompute();
}
