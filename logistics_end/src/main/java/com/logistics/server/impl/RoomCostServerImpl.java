package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.RoomCostMapper;
import com.logistics.server.RoomCostServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class RoomCostServerImpl implements RoomCostServer {
    @Autowired
    private RoomCostMapper roomCostMapper;
    @Override
    @Transactional
    public void addRoomCost(String userId, String deliId, Integer days, Float cost) {
        String id = null;
        RoomCost roomCost = new RoomCost();
        //一直生成。直到没有重复位ID为止
        while (roomCost != null) {
            id = "rc" + UUID.randomUUID().toString().replace('-', 'x').substring(0, 10);
            roomCost = roomCostMapper.selectRoomCostById(id);
        }
        roomCost=new RoomCost();
        roomCost.setId(id);
        roomCost.setDeliId(deliId);
        roomCost.setCost(cost);
        roomCost.setUserId(userId);
        roomCost.setDay(days);
        roomCostMapper.insertRoomCost(roomCost);
    }

    @Override
    public PageBean<RoomCost> selectRoomCostByUserId(Integer pageNum, Integer pageSize,String userId, String deliId, String status,String goodsName) {
        PageHelper.startPage(pageNum, pageSize);

        Integer role=(Integer) ((HashMap<String,Object>)ThreadLocalUtils.get()).get("role");
        List<RoomCost> roomCostList=null;
        if(role==0){
            String areaId=(String) ((HashMap<String,Object>)ThreadLocalUtils.get()).get("areaId");
            roomCostList =roomCostMapper.selectRoomCostListForAdmin(userId,deliId,status,goodsName,areaId);
        }else{
            userId=(String) ((HashMap<String,Object>)ThreadLocalUtils.get()).get("id");
            roomCostList= roomCostMapper.selectRoomCostList(userId, deliId,  status,goodsName);
        }

        Page<RoomCost> page = (Page<RoomCost>) roomCostList;
        PageBean<RoomCost> roomCostPageBean = new PageBean<>();
        roomCostPageBean.setItems(page.getResult());
        roomCostPageBean.setTotal((int) page.getTotal());

        return roomCostPageBean;
    }

    @Override
    public void updatePriceRoomCost(RoomCost roomCost) {
        roomCostMapper.updateRoomCost(roomCost);
    }

    @Override
    public RoomCost detailRoomCost(String id) {
        return  roomCostMapper.selectDetailRoomCostById(id);
    }

    @Override
    public List<RoomCost> InComeLastDay() {
        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        List<RoomCost> roomCostList = roomCostMapper.selectInComeLastDay(areaId);
        return roomCostList;
    }
}
