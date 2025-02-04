package com.logistics.controller;

import com.logistics.entity.*;
import com.logistics.server.RefrigerateServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/refrigerate")
public class RefrigerateController {
    @Autowired
    RefrigerateServer refrigerateServer;

    @GetMapping("/list")
    public Result getRefrigerateByAreaId(Integer pageNum, Integer pageSize, String name, String id, Integer status) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 8;
        }

        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        PageBean<Refrigerate> refrigeratePageBean = refrigerateServer.getRefrigerateListByAreaId(pageNum, pageSize, areaId, name, id, status);
        return Result.success(refrigeratePageBean);
    }

    @PutMapping
    public Result update(@RequestBody @Validated Refrigerate refrigerate) {
        refrigerateServer.updateRefrigerate(refrigerate);
        return Result.success();
    }

    @PostMapping
    public Result add(@RequestBody @Validated Refrigerate refrigerate) {
        refrigerateServer.addRefrigerate(refrigerate);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result del(@PathVariable String id) {
        refrigerateServer.deleteRefrigerateById(id);
        return Result.success();
    }

    @GetMapping("detail/{id}")
    public Result getDetail(@PathVariable String id) {
        Refrigerate refrigerateById = refrigerateServer.getRefrigerateById(id);
        return Result.success(refrigerateById);
    }
    @PostMapping("/inRoom")
    public Result goodsInRoom(@RequestBody Rm_Gs rmGs){
        refrigerateServer.inRoom(rmGs);
        return Result.success();
    }
}
