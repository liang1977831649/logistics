package com.logistics.controller;

import com.logistics.entity.Result;
import com.logistics.entity.RoomCostCompute;
import com.logistics.server.RoomCostComputeServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roomCostCompute")
public class RoomCostComputeController {
    @Autowired
    private RoomCostComputeServer roomCostComputeServer;
    @GetMapping("/show")
    public Result getRoomCostCompute(){
        RoomCostCompute roomCostCompute= roomCostComputeServer.getRoomCostCompute();
        return Result.success(roomCostCompute);
    }
    @PutMapping
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result updateRoomCostCompute(@RequestBody RoomCostCompute roomCostCompute){
        roomCostComputeServer.updateRoomCostCompute(roomCostCompute);
        return Result.success();
    }
}
