package com.logistics.server;

import com.logistics.entity.ColdChainCenter;
import com.logistics.entity.PageBean;

import java.util.List;

public interface ColdChainCenterServer {
    List<ColdChainCenter> getColdChainCenterByAreaId(String areaId);

    PageBean<ColdChainCenter> getColdChainCenterByAreaId(Integer pageNum, Integer pageSize, String name, String id);

    void addColdCenter(ColdChainCenter coldChainCenter);

    void updateColdChainCenter(ColdChainCenter coldChainCenter);

    void deleteColdChainCenter(String id);
}
