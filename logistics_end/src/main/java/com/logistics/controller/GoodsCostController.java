package com.logistics.controller;

import com.logistics.entity.Goods;
import com.logistics.entity.GoodsCost;
import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.server.GoodsCostServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/goodsCost")
public class GoodsCostController {
    @Autowired
    private GoodsCostServer goodsCostServer;
    @GetMapping("/list")
    public Result getListBuyer(Integer pageNum,Integer pageSize,String userName,String goodsName,String id,Integer status,Integer type){
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=8;
        }
        String  userId = (String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        PageBean<GoodsCost> list = goodsCostServer.getList(pageNum, pageSize, userName, id, goodsName, status, userId,type);
        return Result.success(list);
    }

    @GetMapping("/detail/{id}")
    public  Result getDetailGoodsCost(@PathVariable String id){
        GoodsCost goodsCost=goodsCostServer.detailGoodsCost(id);
        return Result.success(goodsCost);
    }
    @PutMapping
    public Result updateGoodsCost(@RequestBody GoodsCost goodsCost){
        goodsCostServer.updateGoodsCost(goodsCost);
        return Result.success();
    }
}
