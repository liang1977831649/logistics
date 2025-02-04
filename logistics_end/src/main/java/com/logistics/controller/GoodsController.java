package com.logistics.controller;

import com.logistics.entity.Goods;
import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.server.GoodsServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsServer goodsServer;
    @GetMapping("/list")
    public Result getList(Integer pageNum,Integer pageSize,String name,String id,String userName){
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=8;
        }
        PageBean<Goods> goodsPageBean=null;
        Integer role =(Integer) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("role");
        if(role==0){
            goodsPageBean= goodsServer.getGoodsList(pageNum,pageSize,name,id,userName);
        }
        if(role==1){
            String  userId =(String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
            goodsPageBean = goodsServer.getGoodsListByUserId(pageNum, pageSize, name, id,userId);
        }
        return Result.success(goodsPageBean);
    }
    @GetMapping("/shopping")
    public Result getListByUserForShopping(Integer pageNum,Integer pageSize,String name,String id){
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=8;
        }
        PageBean<Goods> goodsList = goodsServer.getGoodsListForShopping(pageNum, pageSize, name, id);
        return Result.success(goodsList);
    }

    @PostMapping
    public Result addGoods(@RequestBody @Validated Goods goods){
        goodsServer.addGoodsServer(goods);
        return Result.success();
    }

    @PutMapping
    public Result updateGoods(@RequestBody @Validated(Goods.Update.class) Goods goods){
        goodsServer.updateGoodsServer(goods);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable String id){
        goodsServer.deleteGoodsById(id);
        return Result.success();
    }
    @GetMapping("/detail/{id}")
    public Result goodsDetail(@PathVariable String id){
        Goods goodsById = goodsServer.getGoodsById(id);
        return Result.success(goodsById);
    }


}
