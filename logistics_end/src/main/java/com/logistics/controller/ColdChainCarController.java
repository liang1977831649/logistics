package com.logistics.controller;

import com.logistics.entity.ColdChainCar;
import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.server.ColdChainCarServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/coldChainCar")
public class ColdChainCarController {
    @Autowired
    private ColdChainCarServer coldChainCarServer;

    @GetMapping("/list")
    public Result getColdChainCarByAreaId(Integer pageNum, Integer pageSize, String name, String id, Integer status){
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=8;
        }

        String areaId = (String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        PageBean<ColdChainCar> driverPageBean = coldChainCarServer.getColdChainCarListByAreaId(pageNum, pageSize, areaId, name, id,status);
        return Result.success(driverPageBean);
    }
    @PutMapping
    public Result update(@RequestBody @Validated(ColdChainCar.Update.class) ColdChainCar coldChainCar){
        coldChainCarServer.updateColdChainCar(coldChainCar);
        return Result.success();
    }
    @PostMapping
    public Result add(@RequestBody @Validated ColdChainCar coldChainCar){
        coldChainCarServer.addColdChainCar(coldChainCar);
        return Result.success();
    }
    @DeleteMapping("/{id}")
    public Result del(@PathVariable String id){
        coldChainCarServer.deleteColdChainCarById(id);
        return Result.success();
    }
}
