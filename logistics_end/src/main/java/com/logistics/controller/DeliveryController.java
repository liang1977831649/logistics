package com.logistics.controller;

import com.logistics.entity.Delivery;
import com.logistics.entity.Goods;
import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.server.DeliveryServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {
    @Autowired
    DeliveryServer deliveryServer;
    @PostMapping
    public Result addDelivery(@RequestBody @Validated(Delivery.Add.class) Delivery delivery){
        deliveryServer.addDelivery(delivery);
        return Result.success();
    }
    @GetMapping("/list")
    public Result getList(Integer pageNum,Integer pageSize,String id,String goodsName,Integer status){
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=8;
        }
        Integer role = (Integer) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("role");
        String userId=null;
        if(role==1){
            userId=(String) ((HashMap<String,Object>) ThreadLocalUtils.get()).get("id");
        }
        PageBean<Delivery> deliveryPageBean = deliveryServer.getDeliveryList(pageNum, pageSize, id,goodsName,status,userId);

        return Result.success(deliveryPageBean);
    }

    @DeleteMapping("/{id}")
    public Result deleteDelivery(@PathVariable String id){
        deliveryServer.deleteDeliveryById(id);
        return Result.success();
    }

    @PutMapping("/loading")
    public Result updateDelivery(@RequestBody @Validated(Delivery.Loading.class) Delivery delivery){
        deliveryServer.changeLoadingDeliveryToCar(delivery);
        return Result.success();
    }

    @GetMapping("/arrival/{id}")
    public Result arrival(@PathVariable String id){
        deliveryServer.arrival(id);
        return Result.success();
    }

    @GetMapping("/receipt/{id}")
    public Result receiptGoods(@PathVariable  String id){
        deliveryServer.receiptGoods(id);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result detailDelivery(@PathVariable String id){
        Delivery delivery = deliveryServer.detailDelivery(id);
        return Result.success(delivery  );
    }


}
