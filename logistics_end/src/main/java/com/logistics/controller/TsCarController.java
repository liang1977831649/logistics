package com.logistics.controller;

import com.logistics.entity.*;
import com.logistics.server.DeliveryServer;
import com.logistics.server.TransportationServer;
import com.logistics.server.TsCarServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/tsCar")
public class TsCarController {
    @Autowired
    private TsCarServer tsCarServer;


    @GetMapping("/list")
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result getList(Integer pageNum, Integer pageSize, String id, String carId, String driverName, String driverId,Integer status) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 8;
        }

        //这里使用ThreadLocalUtils，并没有使用前端路径参数来统一处理
        //判断是管理员还是用户
        Integer role = (Integer) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("role");
        String userId = null;
        if (role == 1) {
            userId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        }
        PageBean<TsCar>  tsCarPageBean = tsCarServer.getTsCarList(pageNum, pageSize, id, carId, driverName, driverId, userId,status);
        return Result.success(tsCarPageBean);
    }

    @PostMapping
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result addTsCar(@RequestBody TsCar tsCar){
        tsCarServer.addTsCar(tsCar);
        return Result.success();
    }
    @GetMapping("detail/{id}")
    public Result getTsCarById(@PathVariable String id){
        TsCar tsCar= tsCarServer.getTsCarById(id);
        return Result.success(tsCar);
    }
    @PutMapping
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result updateTsCar(@RequestBody @Validated TsCar tsCar){
        tsCarServer.updateTsCar(tsCar);
        return Result.success();
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result deleteTsCar(@PathVariable String id){
        tsCarServer.deleteTsCar(id);
        return Result.success();
    }
    @PostMapping("/departure")
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result departure(@RequestBody TsCar tsCar){
        //判断是运输还是配送
        tsCarServer.departureTsCar(tsCar);
        return Result.success();
    }

}
