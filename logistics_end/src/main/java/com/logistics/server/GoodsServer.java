package com.logistics.server;

import com.logistics.entity.Goods;
import com.logistics.entity.PageBean;

public interface GoodsServer {

    PageBean<Goods> getGoodsListByUserId(Integer pageNum, Integer pageSize, String name, String id, String userId);

    void addGoodsServer(Goods goods);

    void updateGoodsServer(Goods goods);

    void deleteGoodsById(String id);

    Goods getGoodsById(String id);

    PageBean<Goods> getGoodsList(Integer pageNum,Integer pageSize,String name,String id);
}
