package com.logistics.server;

import com.logistics.entity.PageBean;
import com.logistics.entity.Transportation;
import com.logistics.entity.TsCar;

import java.util.List;

public interface TransportationServer {
    void addTransportation(Transportation transportation);


    PageBean<Transportation> getTransportationList(Integer pageNum, Integer pageSize, String id, String goodsName, String userId, String goodsId, Integer status, String carDriId);

    void deleteTransportationById(String id);

    void distributionServer(Transportation transportation);

    //void updateDistribution(Transportation transportation);

}
