package com.logistics.controller;

import com.logistics.entity.ColdChainCenter;
import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.server.ColdChainCenterServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/coldChainCenter")
public class ColdChainCenterController {
    @Autowired
    ColdChainCenterServer coldChainCenterServer;
    @PreAuthorize("@ex.verificationHandler(0)")

    @GetMapping("/list")
    //在新增冷链室，需要选择冷链中心，这里就是获取到所有的当地县域里的冷链中心
    public Result getList() {
        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        List<ColdChainCenter> coldChainCenterByAreaId = coldChainCenterServer.getColdChainCenterByAreaId(areaId);
        return Result.success(coldChainCenterByAreaId);
    }

    @GetMapping("/listAdmin")
    //管理员 管理冷链中心
    @PreAuthorize("@ex.verificationHandler(0)")

    public Result getList(Integer pageNum, Integer pageSize, String name, String id) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 8;
        }
        PageBean<ColdChainCenter> coldChainCenterByAreaId = coldChainCenterServer.getColdChainCenterByAreaId(pageNum, pageSize, name, id);
        return Result.success(coldChainCenterByAreaId);
    }

    @PostMapping
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result add(@RequestBody @Validated ColdChainCenter coldChainCenter) {
        coldChainCenterServer.addColdCenter(coldChainCenter);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result update(@RequestBody @Validated ColdChainCenter coldChainCenter) {
        coldChainCenterServer.updateColdChainCenter(coldChainCenter);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result deleteColdChainCenter(@PathVariable String id) {
        coldChainCenterServer.deleteColdChainCenter(id);
        return Result.success();
    }
}
