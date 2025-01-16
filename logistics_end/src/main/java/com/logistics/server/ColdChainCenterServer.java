package com.logistics.server;

import com.logistics.entity.ColdChainCenter;

import java.util.List;

public interface ColdChainCenterServer {
    List<ColdChainCenter> getColdChainCenterByAreaId(String areaId);

}
