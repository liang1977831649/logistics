package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.server.DeliveryServer;
import com.logistics.server.GoodsCostServer;
import com.logistics.server.RoomCostServer;
import com.logistics.utils.ComputeUtils;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class DeliveryServerImpl implements DeliveryServer {
    @Autowired
    private DeliveryMapper deliveryMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSererImpl goodsSererImpl;
    @Autowired
    private TsCarMapper tsCarMapper;
    @Autowired
    private ColdChainCarServerImpl coldChainCarServerImpl;
    @Autowired
    private TransportationMapper transportationMapper;
    @Autowired
    private TsCarServerImpl tsCarServerImpl;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GoodsCostServer goodsCostServer;
    @Autowired
    private Rm_GsMapper rmGsMapper;
    @Autowired
    private RoomCostServer roomCostServer;

    @Override
    @Transactional
    public void addDelivery(Delivery delivery) {
        //检验是否还有重量以及是否还在出售
        Goods goods = goodsMapper.selectGoodsById(delivery.getGoodsId());
        if (goods.getStatus() != 4 || goods.getWeight() == 0) {
            throw new RuntimeException("该物品不被销售或已售罄！");
        }
        String id = null;
        Delivery deliveryNew = new Delivery();
        //一直生成。直到没有重复位ID为止
        while (deliveryNew != null) {
            id = "de" + UUID.randomUUID().toString().replace('-', 'x').substring(0, 10);
            deliveryNew = deliveryMapper.selectDeliveryById(id);

        }
        //基本设置
        delivery.setId(id);
        delivery.setUserId((String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("id"));
        Float resultVolume = ComputeUtils.computeVolumeByWeight(goods.getVolume(), goods.getWeight(), delivery.getWeight());
        delivery.setVolume(resultVolume);

        //添加
        deliveryMapper.insertDelivery(delivery);

        //减掉商品的体积和重量；
        goodsSererImpl.changeGoodsVolumeAndWeight(delivery.getGoodsId(), resultVolume, delivery.getWeight(), 0);
    }

    @Override
    public PageBean<Delivery> getDeliveryList(Integer pageNum, Integer pageSize, String id, String goodsName, Integer status, String userId) {
        PageHelper.startPage(pageNum, pageSize);
        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");

        List<Delivery> deliveryList = deliveryMapper.selectDelivery(id, goodsName, status, userId,areaId);

        Page<Delivery> page = (Page<Delivery>) deliveryList;
        PageBean<Delivery> deliveryPageBean = new PageBean<>();
        deliveryPageBean.setItems(page.getResult());
        deliveryPageBean.setTotal((int) page.getTotal());

        return deliveryPageBean;
    }

    @Override
    @Transactional
    public void deleteDeliveryById(String id) {
        //验证是否存在
        //业务一：增加农产品的变化量
        Delivery delivery = deliveryMapper.selectDeliveryById(id);
        if (delivery == null) {
            throw new RuntimeException("该配送单号不存在");
        }
        if (delivery.getStatus() != 1) {
            throw new RuntimeException("该配送单状态是正在运输或已送达，不可删除");
        }
        goodsSererImpl.changeGoodsVolumeAndWeight(delivery.getGoodsId(), delivery.getVolume(), delivery.getWeight(), 1);

        //删除编号
        deliveryMapper.deleteDeliveryById(id);

        if (delivery.getCarDriId() == null) {
            return;
        }
        //业务二：减少对应车辆的载重和体积，因为是取消了配送
        TsCar tsCar = tsCarMapper.selectTsCarById(delivery.getCarDriId());
        coldChainCarServerImpl.changeCarWeightAndWeight(tsCar.getCarId(), delivery.getVolume(), delivery.getWeight(), 0);

    }

    //装配以及更改装配的业务，其实也是同一种操作
    @Transactional
    @Override
    public void changeLoadingDeliveryToCar(Delivery delivery) {
        //业务一：查询是否有这个delivery
        Delivery deliveryOld = deliveryMapper.selectDeliveryById(delivery.getId());
        if (deliveryOld == null) {
            throw new RuntimeException("不存在该配送单");
        }
        //情况（1）：如果carDri是空的，那就有一种可能：取消装车
        if (StringUtil.isNullOrEmpty(delivery.getCarDriId())) {
            if (!StringUtil.isNullOrEmpty(deliveryOld.getCarDriId())) {
                unLoadingCar(deliveryOld);
            }
            return;
        }

        //细节：如果这个carDriId不为空，那就再判断这个货车是否装有运输子单的业务。这个方法解决的是配送，不是运输
        List<Transportation> transportationList = transportationMapper.selectTransportationListByTsCarId(delivery.getCarDriId());
        if (!transportationList.isEmpty()) {
            throw new RuntimeException("该配送总单下有运输子单,不可选择");
        }

        //情况（2）：deliveryOld的carDriId不存在,说明是"装车业务"
        if (StringUtil.isNullOrEmpty(deliveryOld.getCarDriId())) {
            loadingCar(delivery);
        }
        //如果存在，就说明是”改车业务“
        else {
            if (delivery.getCarDriId().equals(deliveryOld.getCarDriId())) {
                throw new RuntimeException("更改的配送单与原来的相同!");
            }
            //本质上就是，先卸货，再装车
            unLoadingCar(deliveryOld);
            loadingCar(delivery);
        }
    }

    @Override
    public void arrival(String id) {
        //业务一：获取并检查,改变Delivery状态
        Delivery deliveryById = deliveryMapper.selectDeliveryById(id);
        if (deliveryById == null) {
            throw new RuntimeException("配送单不存在");
        }
        if (deliveryById.getStatus() != 2) {
            throw new RuntimeException("配送单状态不是运输中，不可抵达");
        }

        deliveryById.setStatus(3);
        deliveryMapper.updateDelivery(deliveryById);

        //业务二：改变冷链车的载重和体积
        TsCar tsCar = tsCarMapper.selectTsCarById(deliveryById.getCarDriId());
        coldChainCarServerImpl.changeCarWeightAndWeight(tsCar.getCarId(), deliveryById.getVolume(), deliveryById.getWeight(), 0);

        //业务三：看该TsCar单子下，是否还有其他的子单，如果没有，就设置状态为空闲
        tsCarServerImpl.setTsCarStatusToOneForDelivery(tsCar);

    }

    @Override
    @Transactional
    public void receiptGoods(String id) {
        //获取delivery
        Delivery delivery = deliveryMapper.selectDeliveryById(id);
        if (delivery == null) {
            throw new RuntimeException("不存在该配送单");
        }
        if (delivery.getStatus() != 3) {
            throw new RuntimeException("该订单为抵达，不可收货");
        }
        if(delivery.getDepartTime()==null){
            throw new RuntimeException("无发车时间，不可收货");
        }

        //业务一：修改状态
        delivery.setStatus(4);
        deliveryMapper.updateDelivery(delivery);

        //业务二：产生商品费用单
        User user = userMapper.selectUserByGoodsId(delivery.getGoodsId());
        Goods goods = goodsMapper.selectGoodsById(delivery.getGoodsId());
        goodsCostServer.addGoodsCost(delivery.getUserId(), user.getId(), delivery.getId(), ComputeUtils.computeCostForGoods(delivery.getWeight(), goods.getPrice()));

        //业务三：产生冷藏室服务费用单
        Rm_Gs rmGsByGoodsId = rmGsMapper.selectRm_GsByGoodsId(delivery.getGoodsId());
        Integer days = ComputeUtils.computeLocalDateTimeSub(rmGsByGoodsId.getCreateTime(), delivery.getDepartTime());
        Float cost = ComputeUtils.computeCostForRoom(delivery.getVolume(), delivery.getWeight(), days);
        roomCostServer.addRoomCost(goods.getUserId(),delivery.getId(),days,cost);
    }

    @Override
    public Delivery detailDelivery(String id) {
        Delivery delivery = deliveryMapper.selectDeliveryById(id);
        return delivery;
    }

    //装货
    @Transactional
    public void loadingCar(Delivery delivery) {
        //业务一：判断CarDri是否存在以及空闲
        TsCar tsCar = tsCarMapper.selectTsCarById(delivery.getCarDriId());
        if (tsCar == null || tsCar.getStatus() != 1) {
            throw new RuntimeException("不可选择该配送总单，不存在或繁忙");
        }
        //业务二：改变货车的容量，增加delivery的volume和weight
        coldChainCarServerImpl.changeCarWeightAndWeight(tsCar.getCarId(), delivery.getVolume(), delivery.getWeight(), 1);

        //业务三：改变delivery订单的订单状态以及carDriId
        delivery.setStatus(1);
        deliveryMapper.updateDelivery(delivery);
    }

    //卸货
    @Transactional
    public void unLoadingCar(Delivery deliveryOld) {
        //选择Old是因为，只有Old才会有原来的carDriId，不然不叫”取消装车“
        //获取对应当车辆，减去容量，更新
        TsCar tsCar = tsCarMapper.selectTsCarById(deliveryOld.getCarDriId());
        coldChainCarServerImpl.changeCarWeightAndWeight(tsCar.getCarId(), deliveryOld.getVolume(), deliveryOld.getWeight(), 0);

        //更新delivery
        deliveryOld.setCarDriId("");
        deliveryMapper.updateDelivery(deliveryOld);
    }

}
