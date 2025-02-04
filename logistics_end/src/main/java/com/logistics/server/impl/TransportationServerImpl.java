package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.server.TransportationServer;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class TransportationServerImpl implements TransportationServer {
    @Autowired
    private TransportationMapper transportationMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private TsCarMapper tsCarMapper;

    @Autowired
    private ColdChainCarServerImpl coldChainCarServerImpl;
    @Autowired
    private DeliveryMapper deliveryMapper;

    @Override
    public void addTransportation(Transportation transportation) {
        Goods goods = goodsMapper.selectGoodsById(transportation.getGoodsId());
        if (goods.getStatus() != 1) {
            throw new RuntimeException("该农产品不在空闲状态，不可添加");
        }

        Transportation selectTransportationByGoodsId = transportationMapper.selectTransportationByGoodsId(transportation.getGoodsId());
        if (selectTransportationByGoodsId != null) {
            throw new RuntimeException("该农产品已经在运输单号中");
        }

        String id = null;
        Transportation transportationNew = new Transportation();
        //一直生成。直到没有重复位ID为止
        while (transportationNew != null) {
            id = "ts" + UUID.randomUUID().toString().replace('-', 'x').substring(0, 10);
            transportationNew = transportationMapper.selectTransportationById(id);
        }
        transportation.setId(id);
        String userId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");
        transportation.setUserId(userId);

        transportationMapper.insertTransportationByUser(transportation);
    }

    @Override
    public PageBean<Transportation> getTransportationList(Integer pageNum, Integer pageSize, String id, String goodsName, String userId, String goodsId, Integer status, String carDriId) {
        PageHelper.startPage(pageNum, pageSize);
        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        List<Transportation> transportationList = transportationMapper.selectTransportationList(id, goodsName, userId, goodsId, status, carDriId, areaId);

        //向下接口转型
        Page<Transportation> page = (Page<Transportation>) transportationList;
        PageBean<Transportation> transportationPageBean = new PageBean<>();
        transportationPageBean.setItems(page.getResult());
        transportationPageBean.setTotal((int) page.getTotal());
        return transportationPageBean;
    }

    @Override
    @Transactional
    public void deleteTransportationById(String id) {
        Transportation transportation = transportationMapper.selectTransportationById(id);
        if (transportation.getStatus() != 1) {
            throw new RuntimeException("该订单状态处于运输或储存中，不可删除");
        }
        //如果为空闲状态，但又有冷链总单Id号的。
        if (!StringUtil.isNullOrEmpty(transportation.getCarDriId())) {
            //业务一：获取到这个农产品对象和对应的冷链货车对象，减少农产品的体积和重量在冷链车的容量和子啊中
            //reduceCarVolumeAndWeight(transportation);
            Goods goods = goodsMapper.selectGoodsById(transportation.getGoodsId());
            TsCar tsCar = tsCarMapper.selectTsCarById(transportation.getCarDriId());
            coldChainCarServerImpl.changeCarWeightAndWeight(tsCar.getCarId(), goods.getVolume(), goods.getWeight(), 0);
        }

        //业务二：更新表
        transportationMapper.deleteTransportationById(id);
    }

    @Transactional
    @Override //装配操作，也可能装配的CarDriId是空的
    public void distributionServer(Transportation transportation) {
        //获取到transportationOld
        Transportation transportationOld = transportationMapper.selectTransportationById(transportation.getId());
        if(transportationOld==null){
            throw new RuntimeException("该运输子弹不存在");
        }
        //业务一：取消装配操作,条件是transportation的carDriId为空
        //判断Transportation
        if (StringUtil.isNullOrEmpty(transportation.getCarDriId())) {
            if(!StringUtil.isNullOrEmpty(transportationOld.getCarDriId())){
                cancelDistribute(transportationOld);
                return;
            }
        }
        //如果不是空，那就查看tsCar是否有其他单号
        List<Delivery> deliveryList = deliveryMapper.selectDeliveryByTsCarId(transportation.getCarDriId());
        if(!deliveryList.isEmpty()){
            throw new RuntimeException("该订单下有配送子单，不可运输");
        }

        //业务二：装配业务
        if(StringUtil.isNullOrEmpty( transportationOld.getCarDriId())){
            distribute(transportation);
        }
        //更改装配业务
        else{
            if(transportation.getCarDriId().equals(transportationOld.getCarDriId())){
                throw new RuntimeException("更改的运输子单与原来相同，无需再更改");
            }
            cancelDistribute(transportationOld);
            distribute(transportation);
        }
        //业务一：根据tsCar的id，查询这个总订单对象的状态是否空闲
        //TsCar tsCar = tsCarMapper.selectTsCarById(transportation.getCarDriId());

        //业务二：
        // 情况（1）根据总订单对象，获取司机和货车对应的对象（一般来说都会有这个对象）,他们的状态是否为空闲
        // 情况（2）如果不需要配置冷链车辆，就直接之置空

        //情况(1):货车的容量能否满足货物容量
        //if (!StringUtil.isNullOrEmpty(transportation.getCarDriId())) {
        //    if (tsCar == null || tsCar.getStatus() != 1) {
        //        throw new RuntimeException("总订单发生错误");
        //    }
        //    Goods goodsById = goodsMapper.selectGoodsById(transportation.getGoodsId());
        //    //改变货车的容量
        //    coldChainCarServerImpl.changeCarWeightAndWeight(tsCar.getCarId(), goodsById.getVolume(), goodsById.getWeight(), 1);
        //}
        //
        ////业务三：改变状态:(1)改变订单运输单状态和tsCarId
        //Transportation transportationNew = new Transportation();
        //transportationNew.setId(transportation.getId());
        //transportationNew.setCarDriId(transportation.getCarDriId());
        //
        //transportationMapper.updateTransportation(transportationNew);
    }



    //@Transactional
    //@Override //更改装配操作,也包含了取消装配的操作
    //public void updateDistribution(Transportation transportation) {
    //    //业务1.判断新加入的CarDriId是否和原来的相同，就是检查是否重复装配
    //    Transportation selectTransportationByGoodsId = transportationMapper.selectTransportationById(transportation.getId());
    //    if (selectTransportationByGoodsId.getCarDriId().equals(transportation.getCarDriId())) {
    //        throw new RuntimeException("请勿重复选定同一运输总单");
    //    }
    //
    //    //业务2.先找到原来的transportation对应的货车，减掉对应的冷链车的容量和载重
    //    TsCar tsCar = tsCarMapper.selectTsCarById(selectTransportationByGoodsId.getCarDriId());
    //    Goods goods = goodsMapper.selectGoodsById(selectTransportationByGoodsId.getGoodsId());
    //    coldChainCarServerImpl.changeCarWeightAndWeight(tsCar.getCarId(), goods.getVolume(), goods.getWeight(), 0);
    //
    //    //业务3。获取被选定总订单的货车，看是否满足其体积和载重，若满足则增加，否则直接抛出异常
    //    distributionServer(transportation);
    //}

    @Transactional
    public void cancelDistribute(Transportation transportationOld){
        //定位TsCar,得到car,减去该农产品的重量和体积
        TsCar tsCar = tsCarMapper.selectTsCarById(transportationOld.getCarDriId());
        Goods goods = goodsMapper.selectGoodsById(transportationOld.getGoodsId());
        coldChainCarServerImpl.changeCarWeightAndWeight(tsCar.getCarId(),goods.getVolume(),goods.getWeight(),0);

        //更新transportation
        transportationOld.setCarDriId("");
        transportationMapper.updateTransportation(transportationOld);
    }
    @Transactional
    public void distribute(Transportation transportation){
        //查询总订单是否存在过
        TsCar tsCar = tsCarMapper.selectTsCarById(transportation.getCarDriId());
        if(tsCar==null||tsCar.getStatus()!=1){
            throw new RuntimeException("不可选择该配送总单，不存在或繁忙");
        }
        //更改冷链车体积和容量
        Goods goods = goodsMapper.selectGoodsById(transportation.getGoodsId());
        coldChainCarServerImpl.changeCarWeightAndWeight(tsCar.getCarId(),goods.getVolume(),goods.getWeight(),1);

        transportationMapper.updateTransportation(transportation);
    }
}
