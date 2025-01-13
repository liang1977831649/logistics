package com.logistics.server;

import com.logistics.entity.ColdChainCar;
import com.logistics.entity.PageBean;
import org.springframework.stereotype.Service;

@Service
public interface ColdChainCarServer {
    PageBean<ColdChainCar> getColdChainCarListByAreaId(Integer pageNum, Integer pageSize, String areaId, String name, String id, Integer status);

    void updateColdChainCar(ColdChainCar coldChainCar);

    void addColdChainCar(ColdChainCar coldChainCar);

    void deleteColdChainCarById(String id);
}
