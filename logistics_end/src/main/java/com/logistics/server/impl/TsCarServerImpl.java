package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.server.TsCarServer;
import com.logistics.utils.SpringUtil;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class TsCarServerImpl implements TsCarServer {
    @Autowired
    private TsCarMapper tsCarMapper;
    @Autowired
    private ColdChainCarMapper coldChainCarMapper;
    @Autowired
    private DriverMapper driverMapper;
    @Autowired
    private TransportationMapper transportationMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private DeliveryMapper deliveryMapper;
    //@Autowired
    //private RefrigerateServerImpl refrigerateServerImpl;
    @Autowired
    private Rm_GsMapper rmGsMapper;

    //解决相互应用依赖问题
    public RefrigerateServerImpl getRefrigerateServerImpl(){
        return SpringUtil.getBean(RefrigerateServerImpl.class);
    }

    @Override
    public PageBean<TsCar> getTsCarList(Integer pageNum, Integer pageSize, String id, String carId, String driverName, String driverId, String userId, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        List<TsCar> tsCarsList = tsCarMapper.selectTsCarList(id, carId, driverName, driverId, userId, status, areaId);
        //向下接口转型
        return getTsCarPageBean(tsCarsList);
    }

    @Override
    @Transactional
    public void addTsCar(TsCar tsCar) {
        String id = null;
        TsCar tsCarNew = new TsCar();
        //一直生成。直到没有重复位ID为止
        while (tsCarNew != null) {
            id = "tc" + UUID.randomUUID().toString().replace('-', 'x').substring(0, 10);
            tsCar.setId(id);
            tsCarNew = tsCarMapper.selectTsCarById(id);
        }

        //判断货车和司机是否繁忙
        Driver driverById = driverMapper.selectDriverById(tsCar.getDriverId());
        ColdChainCar coldChainCar = coldChainCarMapper.selectColdChainCarById(tsCar.getCarId());
        if (driverById == null || driverById.getStatus() != 1) {
            throw new RuntimeException("司机调动错误");
        }
        if (coldChainCar == null || coldChainCar.getStatus() != 1) {
            throw new RuntimeException("货车调动错误");
        }
        if (tsCarMapper.selectTsCarByDriverId(tsCar.getDriverId()) != null) {
            throw new RuntimeException("该司机已被选定");
        }
        if (tsCarMapper.selectTsCarByCarId(tsCar.getCarId()) != null) {
            throw new RuntimeException("该货车已被选定");
        }
        //设置冷链车和司机状态
        driverById.setStatus(2);
        coldChainCar.setStatus(2);
        driverMapper.updateDriver(driverById);
        coldChainCarMapper.updateCar(coldChainCar);


        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        tsCar.setAreaId(areaId);
        tsCarMapper.insertTsCar(tsCar);
    }

    @Override
    public TsCar getTsCarById(String id) {
        TsCar tsCar = tsCarMapper.selectTsCarById(id);
        return tsCar;
    }

    @Override
    @Transactional
    public void updateTsCar(TsCar tsCar) {
        //查询原来的货车的容量是否满足选择
        TsCar tsCarOld = tsCarMapper.selectTsCarById(tsCar.getId());
        if (tsCarOld == null) {
            throw new RuntimeException("不存在该运输单号");
        }
        //查询是否空闲,是否为空，是否满足条件
        Driver driverNew = driverMapper.selectDriverById(tsCar.getDriverId());
        Driver driverOld = driverMapper.selectDriverById(tsCarOld.getDriverId());
        ColdChainCar coldChainCarByNew = coldChainCarMapper.selectColdChainCarById(tsCar.getCarId());
        ColdChainCar coldChainCarByOld = coldChainCarMapper.selectColdChainCarById(tsCarOld.getCarId());

        if (driverNew != null) {
            if (!driverNew.getId().equals(driverOld.getId())) {
                if (driverNew.getStatus() == 1) {
                    driverOld.setStatus(1);
                    driverNew.setStatus(2);
                    driverMapper.updateDriver(driverOld);
                    driverMapper.updateDriver(driverNew);
                } else {
                    throw new RuntimeException("该司机不可选择");
                }
            }
        } else {
            throw new RuntimeException("该司机不可选择");
        }

        if (coldChainCarByNew != null) {
            if (!coldChainCarByNew.getId().equals(coldChainCarByOld.getId())) {
                if (coldChainCarByNew.getStatus() == 1) {
                    coldChainCarByOld.setStatus(1);
                    coldChainCarByNew.setStatus(2);
                    coldChainCarByNew.setCurVolume(coldChainCarByOld.getCurVolume());
                    coldChainCarByNew.setCurWeight(coldChainCarByOld.getCurWeight());
                    coldChainCarByOld.setCurVolume(Float.valueOf(0));
                    coldChainCarByOld.setCurWeight(Float.valueOf(0));
                    coldChainCarMapper.updateCar(coldChainCarByOld);
                    coldChainCarMapper.updateCar(coldChainCarByNew);
                } else {
                    throw new RuntimeException("该货车不可选择");
                }
            }
        } else {
            throw new RuntimeException("该司机不可选择");
        }
        tsCarMapper.update(tsCar);
    }

    @Override
    @Transactional
    public void deleteTsCar(String id) {
        TsCar tsCar = tsCarMapper.selectTsCarById(id);
        if (tsCar == null) {
            throw new RuntimeException("不可删除");
        }
        //校验该运输总单，是否有货物
        //如果有货物，不能删除
        List<Transportation> transportationList = transportationMapper.selectTransportationByTsCarId(id);
        if (transportationList != null && transportationList.size() != 0) {
            throw new RuntimeException("该运输总单下有运输子单，不可删除");
        }
        List<Delivery> deliveryList = deliveryMapper.selectDeliveryByTsCarId(tsCar.getId());
        if(deliveryList!=null&&!deliveryList.isEmpty()){
            throw new RuntimeException("该运输总单下有配送子单，不可删除");
        }

        //获取运输总单的司机和货车，将他们的状态设置为空闲
        Driver driverById = driverMapper.selectDriverById(tsCar.getDriverId());
        ColdChainCar coldChainCarById = coldChainCarMapper.selectColdChainCarById(tsCar.getCarId());
        driverById.setStatus(1);
        coldChainCarById.setStatus(1);
        driverMapper.updateDriver(driverById);
        coldChainCarMapper.updateCar(coldChainCarById);
        tsCarMapper.delete(id);
    }

    @Override
    @Transactional
    //发车操作
    public void departureTsCar(TsCar tsCar) {
        List<Transportation> transportationList = transportationMapper.selectTransportationByTsCarId(tsCar.getId());
        if(transportationList!=null&& !transportationList.isEmpty()){
            departureTsCarForTransportation(transportationList,tsCar);
            return;
        }
        List<Delivery> deliveryList = deliveryMapper.selectDeliveryByTsCarId(tsCar.getId());
        if(deliveryList!=null&&!deliveryList.isEmpty()){
            departureTsCarForDelivery(deliveryList,tsCar);
            return;
        }
        throw new RuntimeException("该冷链车下没有任何子单，不可配送");
    }

    @Transactional
    public void departureTsCarForTransportation(List<Transportation> transportationList,TsCar tsCar) {
        //业务一：获取到运输总单的运输子单
        //List<Transportation> transportationList = transportationMapper.selectTransportationListByTsCarId(tsCar.getId());
        ////判断是否有运输子单，如果有，则修改
        //// transportation的状态和更新时间、goods的状态
        //if (transportationList.isEmpty()) {
        //    throw new RuntimeException("该订单下没有子单，不能发车");
        //}
        Goods goods;
        for (Transportation transportation : transportationList) {
            goods = goodsMapper.selectGoodsById(transportation.getGoodsId());
            goods.setStatus(2);//更改为运输中
            transportation.setStatus(2);//更改为运输中
            transportationMapper.updateTransportation(transportation);
            goodsMapper.updateGoods(goods);
        }
        departureCommon(tsCar);

        ////业务二：获取到冷链货车对象：Car
        //ColdChainCar coldChainCarById = coldChainCarMapper.selectColdChainCarById(tsCar.getCarId());
        //coldChainCarById.setStatus(3);
        //coldChainCarMapper.updateCar(coldChainCarById);
        ////业务三：获取到司机对象：dri
        //Driver driverById = driverMapper.selectDriverById(tsCar.getDriverId());
        //driverById.setStatus(3);
        //driverMapper.updateDriver(driverById);
        //
        ////业务业务四：更改tsCar的状态
        //tsCar.setStatus(2);
        //tsCarMapper.update(tsCar);
    }
    @Transactional
    public synchronized void departureTsCarForDelivery(List<Delivery> deliveryList,TsCar tsCar){
        for (Delivery delivery : deliveryList) {
            //业务一：更新delivery，一个发车时间，另一个是状态
            delivery.setStatus(2);
            delivery.setDepartTime(LocalDateTime.now());
            deliveryMapper.updateDelivery(delivery);

            //这里要加锁的原因解读：如果不加锁，会发生：
            //（0）甲乙两个管理员的线程都同时进入到了这个方法
            //（1）假设甲管理员执行完了rmGsMapper.deleteByGoodsId(goods.getId());这一条代码,删除id=1001的记录
            //（2）而乙管理员正想要获取Rm_goods的id=1001这题条记录，但恰好这条纪录被甲删除了，因此会发生异常

            //业务二：从冷链室中减掉delivery对应的容量
            Rm_Gs rmGsByGoodsId = rmGsMapper.selectRm_GsByGoodsId(delivery.getGoodsId());
            getRefrigerateServerImpl().changeRoomVolumeOperation(rmGsByGoodsId.getRmId(),delivery.getVolume(),0);

            //业务三：更新完状态之后，检索该商品
            Goods goods = goodsMapper.selectGoodsById(delivery.getGoodsId());
            if(goods.getStatus()==5){//如果是售罄状态
                //检查该农产品在该状态下是否有待发车的Delivery
                List<Delivery> deliveryListIsOneByGoodsId = deliveryMapper.selectDeliveryListIsOneByGoodsId(goods.getId());
                if(deliveryListIsOneByGoodsId.isEmpty()){
                    //不打算删除了，直接更改room_goods的状态
                    rmGsMapper.updateRm_GsStatusByGoodsId(2,goods.getId());
                }
            }
        }
        departureCommon(tsCar);
        //现在问题就是秒如果要删除掉冷链库，货物可能存在怎么办？[上述方案已解决]
    }

    @Transactional
    public void departureCommon(TsCar tsCar){
        //业务二：获取到冷链货车对象：Car
        ColdChainCar coldChainCarById = coldChainCarMapper.selectColdChainCarById(tsCar.getCarId());
        coldChainCarById.setStatus(3);
        coldChainCarMapper.updateCar(coldChainCarById);
        //业务三：获取到司机对象：dri
        Driver driverById = driverMapper.selectDriverById(tsCar.getDriverId());
        driverById.setStatus(3);
        driverMapper.updateDriver(driverById);

        //业务业务四：更改tsCar的状态
        tsCar.setStatus(2);
        tsCarMapper.update(tsCar);
    }

    public PageBean<TsCar> getTsCarPageBean(List<TsCar> goodsList) {
        //向下接口转型
        Page<TsCar> page = (Page<TsCar>) goodsList;
        PageBean<TsCar> goodsPageBean = new PageBean<>();
        goodsPageBean.setItems(page.getResult());
        goodsPageBean.setTotal((int) page.getTotal());
        return goodsPageBean;
    }
    @Transactional
    public void setTsCarStatusToOneForTransportation(TsCar tsCar){
        List<Transportation> transportationList = transportationMapper.selectTransportationByTsCarId(tsCar.getId());
        if (transportationList.isEmpty()) {
            //设置状态
            tsCar.setStatus(1);
            tsCarMapper.update(tsCar);
            //设置司机和冷链货车的状态为2：待发车
            setTsCarStatusToOneCommon(tsCar);
        }
    }
    @Transactional
    public void setTsCarStatusToOneForDelivery(TsCar tsCar){
        List<Delivery> deliveryList = deliveryMapper.selectDeliveryByTsCarIdLimitStatusTow(tsCar.getId());
        if(deliveryList.isEmpty()){
            tsCar.setStatus(1);
            tsCarMapper.update(tsCar);
            //设置司机和冷链货车的状态为2：待发车
            setTsCarStatusToOneCommon(tsCar);
        }
    }
    @Transactional
    public void setTsCarStatusToOneCommon(TsCar tsCar){
        Driver driverById = new Driver();
        driverById.setId(tsCar.getDriverId());
        driverById.setStatus(2);
        ColdChainCar coldChainCarById = new ColdChainCar();
        coldChainCarById.setId(tsCar.getCarId());
        coldChainCarById.setStatus(2);
        driverMapper.updateDriver(driverById);
        coldChainCarMapper.updateCar(coldChainCarById);
    }
}
