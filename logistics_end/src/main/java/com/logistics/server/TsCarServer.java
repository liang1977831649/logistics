package com.logistics.server;

import com.logistics.entity.Goods;
import com.logistics.entity.PageBean;
import com.logistics.entity.Transportation;
import com.logistics.entity.TsCar;

import java.util.List;

public interface TsCarServer {
    PageBean<TsCar> getTsCarList(Integer pageNum, Integer pageSize, String id, String carId, String driverName, String driverId, String userId,Integer status);

    void addTsCar(TsCar tsCar);

    TsCar getTsCarById(String id);

    void updateTsCar(TsCar tsCar);

    void deleteTsCar(String id);



    void departureTsCar(TsCar tsCar);

}
