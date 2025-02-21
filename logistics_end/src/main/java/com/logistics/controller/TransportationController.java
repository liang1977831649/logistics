package com.logistics.controller;

import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.entity.Transportation;
import com.logistics.server.TransportationServer;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/transportation")
public class TransportationController {
    @Autowired
    private TransportationServer transportationServer;
    @PostMapping
    public Result addTransportation(@RequestBody @Validated(Transportation.Add.class) Transportation transportation){
        transportationServer.addTransportation(transportation);
        return Result.success();
    }

    /**
     *
     * @param pageNum
     * @param pageSize
     * @param id
     * @param goodsName 只有用户访问时才提供
     * @param goodsId   用户和管理员访问时提供
     * @param status    只有管理员访问时才提供
     * @param carDriId  只有管理员访问时才提供
     * @return
     */
    @GetMapping("/list")
    public Result getListTransportation(Integer pageNum,Integer pageSize,String id,String goodsName,String goodsId,Integer status,String carDriId) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 8;
        }
        Integer role = (Integer) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("role");
        String userId = null;
        if (role == 1) {
            userId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        }
        PageBean<Transportation> transportationList = transportationServer.getTransportationList(pageNum, pageSize, id, goodsName, userId, goodsId, status, carDriId);
        return Result.success(transportationList);
    }

    @DeleteMapping("/{id}")
    public Result deleteTransportation(@PathVariable String id){
        transportationServer.deleteTransportationById(id);
        return Result.success();
    }
    @PutMapping
    public Result updateTransportation(@Validated(Transportation.Update.class) Transportation transportation){
        return null;
    }

    @PutMapping("/distribution")
    //装配
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result distribution(@RequestBody @Validated(Transportation.Update.class) Transportation transportation){
        //if(StringUtil.isNullOrEmpty(transportation.getCarDriId())){
        //    throw new RuntimeException("运输总单号不能为空");
        //}
        transportationServer.distributionServer(transportation);
        return Result.success();
    }
    //@PutMapping("/updateDistribution")
    //public Result updateDistribution(@RequestBody @Validated(Transportation.Update.class) Transportation transportation){
    //    transportationServer.updateDistribution(transportation);
    //    return Result.success();
    //}
}
