package com.logistics.server;

import com.logistics.entity.Delivery;
import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.entity.TsCar;

import java.util.List;

public interface DeliveryServer {
    void addDelivery(Delivery delivery);

    PageBean<Delivery> getDeliveryList(Integer pageNum, Integer pageSize, String id, String goodsName, Integer status, String userId);

    void deleteDeliveryById(String id);

    void changeLoadingDeliveryToCar(Delivery delivery);


    void arrival(String id);

    void receiptGoods(String id);

    Delivery detailDelivery(String id);
}
