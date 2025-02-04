package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.server.RefrigerateServer;
import com.logistics.server.TransportationServer;
import com.logistics.utils.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RefrigerateServerImpl implements RefrigerateServer {
    @Autowired
    private RefrigerateMapper refrigerateMapper;
    @Autowired
    private TransportationMapper transportationMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private Rm_GsMapper rmGsMapper;
    @Autowired
    private ColdChainCarServerImpl coldChainCarServerImpl;
    //@Autowired
    //private TsCarServerImpl tsCarServerImpl;
    @Autowired
    private TsCarMapper tsCarMapper;

    //解决相互引用依赖问题
    public TsCarServerImpl getTsCarServerImpl(){
        return SpringUtil.getBean(TsCarServerImpl.class);
    }

    @Override
    public PageBean<Refrigerate> getRefrigerateListByAreaId(Integer pageNum, Integer pageSize, String areaId, String name, String id, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Refrigerate> refrigerate = refrigerateMapper.selectRefrigerateByAreaId(areaId, name, id, status);
        //向下接口转型
        Page<Refrigerate> page = (Page<Refrigerate>) refrigerate;
        PageBean<Refrigerate> refrigeratePageBean = new PageBean<>();
        refrigeratePageBean.setItems(page.getResult());
        refrigeratePageBean.setTotal((int) page.getTotal());
        return refrigeratePageBean;
    }

    @Override
    public void updateRefrigerate(Refrigerate refrigerate) {
        Refrigerate refrigerateById = refrigerateMapper.selectRefrigerateById(refrigerate.getId());
        if (refrigerateById == null) {
            throw new RuntimeException("不存在该Id的冷链室");
        }
        refrigerateMapper.updateRefrigerate(refrigerate);
    }

    @Override
    public void addRefrigerate(Refrigerate refrigerate) {
        Refrigerate refrigerateById = refrigerateMapper.selectRefrigerateById(refrigerate.getId());
        if (refrigerateById != null) {
            throw new RuntimeException("已存在该Id的冷链室");
        }
        refrigerateMapper.insertAddRefrigerate(refrigerate);
    }

    @Override
    public void deleteRefrigerateById(String id) {
        Refrigerate refrigerateById = refrigerateMapper.selectRefrigerateById(id);
        if (refrigerateById == null) {
            throw new RuntimeException("不存在该Id的冷链室,不能删除");
        }
        if (refrigerateById.getStatus() != 1) {
            throw new RuntimeException("该冷链室有冷链品，不可删除");
        }
        refrigerateMapper.deleteRefrigerateById(id);
    }

    @Override
    public Refrigerate getRefrigerateById(String id) {
        return refrigerateMapper.selectRefrigerateById(id);
    }

    @Override
    @Transactional
    public void inRoom(Rm_Gs rmGs) {
        //检查该商品是否已经存在库存里了
        Goods goods = goodsMapper.selectGoodsById(rmGs.getGdId());
        Rm_Gs rmGsByGoodsId = rmGsMapper.selectRm_GsByGoodsId(goods.getId());
        if(rmGsByGoodsId!=null){
            throw new RuntimeException("改农产品已经入库");
        }

        //业务一：冷链车的体积和重量减掉对应农产品的体积和重量
        Transportation transportationByGoodsId = transportationMapper.selectTransportationByGoodsId(rmGs.getGdId());
        //transportationServerImpl.reduceCarVolumeAndWeight(transportationByGoodsId);
        TsCar tsCar = tsCarMapper.selectTsCarById(transportationByGoodsId.getCarDriId());
        coldChainCarServerImpl.changeCarWeightAndWeight(tsCar.getCarId(),goods.getVolume(),goods.getWeight(),0);

        //业务二：入室操作：将农产品的存入冷链室中,增加其对应的体积，并增加Room_goods表的一条记录
        inRoomOperation(rmGs);

        //业务三：改变农产品的状态为3：在库
        goods.setStatus(3);
        goodsMapper.updateGoods(goods);

        //业务四：设置transportation子单的状态为3：已入室
        transportationByGoodsId.setStatus(3);
        transportationMapper.updateTransportation(transportationByGoodsId);

        //业务五：排查TsCar下面是否没有入库的未入库的transportation单号，如果没有，那就直接设置tsCar的状态为1:空闲
        getTsCarServerImpl().setTsCarStatusToOneForTransportation(tsCar);

    }

    //入室操作
    @Transactional
    public void inRoomOperation(Rm_Gs rmGs) {
        Refrigerate refrigerate = refrigerateMapper.selectRefrigerateById(rmGs.getRmId());
        Goods goods = goodsMapper.selectGoodsById(rmGs.getGdId());
        Float curVolume = refrigerate.getCurVolume() + goods.getVolume();
        if (refrigerate.getMaxVolume() < curVolume) {
            throw new RuntimeException("冷链室容量不足");
        }
        refrigerate.setCurVolume(curVolume);
        refrigerate.setStatus(2);
        refrigerateMapper.updateRefrigerate(refrigerate);

        //增加记录
        rmGsMapper.addRmGs(rmGs);
    }
    //出库操作
    @Transactional
    //当冷链车发车配送后，调用此方法
    public void changeRoomVolumeOperation(String refrigerateId,Float volume,int type){
        Refrigerate refrigerate = refrigerateMapper.selectRefrigerateById(refrigerateId);
        if(type==0){
            refrigerate.setCurVolume(refrigerate.getCurVolume()-volume);
        }else{
            Float result=refrigerate.getCurVolume()+volume;
            if(result>refrigerate.getMaxVolume()){
                throw new RuntimeException("冷链室空闲不足够已装载");
            }
            refrigerate.setCurVolume(result);
        }
        //更新
        refrigerateMapper.updateRefrigerate(refrigerate);
    }
}
