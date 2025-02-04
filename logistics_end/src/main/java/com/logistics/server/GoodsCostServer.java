package com.logistics.server;

import com.logistics.entity.GoodsCost;
import com.logistics.entity.PageBean;

public interface GoodsCostServer {
    void addGoodsCost (String buyerId, String salesId,String deliId, Float cost);

    PageBean<GoodsCost> getList(Integer pageNum, Integer pageSize, String userName, String id, String goodsName, Integer status, String userId,Integer type);

    GoodsCost detailGoodsCost(String id);

    void updateGoodsCost(GoodsCost goodsCost);
}
