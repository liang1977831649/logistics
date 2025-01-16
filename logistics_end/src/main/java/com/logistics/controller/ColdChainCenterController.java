package com.logistics.controller;

import com.logistics.entity.ColdChainCenter;
import com.logistics.entity.Result;
import com.logistics.server.ColdChainCenterServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/coldChainCenter")
public class ColdChainCenterController {
    @Autowired
    ColdChainCenterServer coldChainCenterServer;
    @GetMapping("/list")
    public Result getList(){
        String areaId =(String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        List<ColdChainCenter> coldChainCenterByAreaId = coldChainCenterServer.getColdChainCenterByAreaId(areaId);
        return Result.success(coldChainCenterByAreaId);
    }
}
