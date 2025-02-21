package com.logistics.controller;

import com.logistics.entity.*;
import com.logistics.server.DriverServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/driver")
public class DriverController {
    @Autowired
    private DriverServer driverServer;

    @GetMapping("/list")
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result getDriverByAreaId(Integer pageNum, Integer pageSize, String name, String id,Integer status){
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=8;
        }

        String areaId = (String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        PageBean<Driver> driverPageBean = driverServer.getUserListByAreaId(pageNum, pageSize, areaId, name, id,status);
        return Result.success(driverPageBean);
    }
    @PutMapping
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result update(@RequestBody @Validated Driver driver){
        driverServer.updateDriver(driver);
        return Result.success();
    }
    @PostMapping
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result add(@RequestBody @Validated Driver driver){
        driverServer.addDriver(driver);
        return Result.success();
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result del(@PathVariable String id){
        driverServer.deleteDriverById(id);
        return Result.success();
    }
}
