package com.logistics.server;

import com.logistics.entity.ColdChainCenter;

public interface ColdChainCenterServer {
    ColdChainCenter getColdChainCenterByAreaId(String areaId);
}
