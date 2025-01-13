package com.logistics.controller;

import com.logistics.entity.Result;
import com.logistics.server.AreasServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.geom.Area;
import java.util.List;

@RestController
@RequestMapping("/area")
public class AreaController {
    @Autowired
    AreasServer areasServer;
    @GetMapping("/getArea/{areaId}")
    public Result getAreaList(@PathVariable String areaId){
        List<String> provinceIdAndCityIdAndAreaIdByAreaId = areasServer.getProvinceIdAndCityIdAndAreaIdByAreaId(areaId);
        return Result.success(provinceIdAndCityIdAndAreaIdByAreaId);
    }
}
