package com.logistics.server.impl;

import com.logistics.entity.ColdChainCenter;
import com.logistics.mapper.ColdChainCenterMapper;
import com.logistics.server.ColdChainCenterServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColdChainCenterServerImpl implements ColdChainCenterServer {

    @Autowired
    ColdChainCenterMapper coldChainCenterMapper;
    public ColdChainCenter getColdChainCenterByAreaId(String areaId){
        return coldChainCenterMapper.getColdChainCenterByAreaId(areaId);
    }
}
