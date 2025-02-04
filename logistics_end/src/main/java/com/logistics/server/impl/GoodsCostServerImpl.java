package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.GoodsCostMapper;
import com.logistics.mapper.UserMapper;
import com.logistics.server.GoodsCostServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class GoodsCostServerImpl implements GoodsCostServer {
    @Autowired
    private GoodsCostMapper goodsCostMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void addGoodsCost(String buyerId, String salesId,String deliId, Float cost){
        String id = null;
        GoodsCost goodsCost = new GoodsCost();
        //一直生成。直到没有重复位ID为止
        while (goodsCost != null) {
            id = "gc" + UUID.randomUUID().toString().replace('-', 'x').substring(0, 10);
            goodsCost = goodsCostMapper.selectGoodsCostById(id);
        }
        goodsCost=new GoodsCost();
        goodsCost.setId(id);
        goodsCost.setBuyerId(buyerId);
        goodsCost.setSalesId(salesId);
        goodsCost.setDeliId(deliId);
        goodsCost.setCost(cost);

        goodsCostMapper.insertGoodsCost(goodsCost);
    }

    @Override
    public PageBean<GoodsCost> getList(Integer pageNum, Integer pageSize, String userName, String id,String goodsName, Integer status, String userId,Integer type) {
        PageHelper.startPage(pageNum, pageSize);
        List<GoodsCost> goodsCostList=null;
        //type的意思是：1我的支付；2我的收款
        goodsCostList=goodsCostMapper.selectGoodsCostListByUserId(userName,id,goodsName,status,userId,type);
        Page<GoodsCost> page = (Page<GoodsCost>) goodsCostList;
        PageBean<GoodsCost> goodsCostPageBean = new PageBean<>();
        goodsCostPageBean.setItems(page.getResult());
        goodsCostPageBean.setTotal((int) page.getTotal());
        return goodsCostPageBean;
    }

    @Override
    public GoodsCost detailGoodsCost(String id) {
        GoodsCost goodsCost = goodsCostMapper.selectGoodsCostByIdForDetail(id);
        //将另一方的名字赋值进去
        String myId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        User user;
        if(goodsCost.getBuyerId().equals( myId)){//如果我是买家，那就设置卖家
            user = userMapper.selectUserById(goodsCost.getSalesId());
        }else{//如果我是卖家，那就设置买家
            user = userMapper.selectUserById(goodsCost.getBuyerId());
        }
        goodsCost.setUserName(user.getName());
        return goodsCost;
    }

    @Override
    public void updateGoodsCost(GoodsCost goodsCost) {
        goodsCostMapper.updateCostMapper(goodsCost);
    }
}
