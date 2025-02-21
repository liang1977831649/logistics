package com.logistics.controller;

import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.entity.RoomCost;
import com.logistics.server.RoomCostServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roomCost")
public class RoomCostController {
    @Autowired
    private RoomCostServer roomCostServer;
    @GetMapping("/list")
    public Result getList(Integer pageNum, Integer pageSize, String deliId, Integer status,String goodsName,String userId){
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=8;
        }

        PageBean<RoomCost> roomCostPageBean = roomCostServer.selectRoomCostByUserId(pageNum, pageSize,userId,deliId, status, goodsName);
        return Result.success(roomCostPageBean);
    }
    @PutMapping
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result updateRoomCost(@RequestBody RoomCost roomCost){
        roomCostServer.updatePriceRoomCost(roomCost);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result getDetailRoomCost(@PathVariable String id){
        RoomCost roomCost = roomCostServer.detailRoomCost(id);
        return Result.success(roomCost);
    }

    @GetMapping("/incomeLastDay")
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result getInComeLastDay(){
        List<RoomCost> roomCostList = roomCostServer.InComeLastDay();
        return Result.success(roomCostList);
    }
}
