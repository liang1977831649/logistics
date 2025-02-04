package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.GoodsMapper;
import com.logistics.mapper.RefrigerateMapper;
import com.logistics.mapper.Rm_GsMapper;
import com.logistics.mapper.TransportationMapper;
import com.logistics.server.ColdChainCenterServer;
import com.logistics.server.GoodsServer;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class GoodsSererImpl implements GoodsServer {
    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private Rm_GsMapper rmGsMapper;

    @Autowired
    private RefrigerateMapper refrigerateMapper;

    @Autowired
    private TransportationMapper transportationMapper;
    @Autowired
    private RefrigerateServerImpl refrigerateServerImpl;

    @Override
    public PageBean<Goods> getGoodsListByUserId(Integer pageNum, Integer pageSize, String name, String id, String userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> goodsList = goodsMapper.getGoodsListByUserId(name, id,userId);

        return getGoodsPageBean(goodsList);
    }

    @Override
    public PageBean<Goods> getGoodsList(Integer pageNum,Integer pageSize,String name,String id,String userName) {
        PageHelper.startPage(pageNum, pageSize);
        String areaId =(String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        List<Goods> goodsList = goodsMapper.selectGoodsAll(name,id,areaId,userName);
        return getGoodsPageBean(goodsList);
    }

    @Override
    public PageBean<Goods> getGoodsListForShopping(Integer pageNum, Integer pageSize, String name, String id) {
        PageHelper.startPage(pageNum, pageSize);
        HashMap<String, Object> hashMap=  ThreadLocalUtils.get();
        String areaId =(String) hashMap.get("areaId");
        String userId =(String) hashMap.get("id");
        List<Goods> goodsList = goodsMapper.selectGoodsAllForShopping(name,id,areaId,userId);
        return getGoodsPageBean(goodsList);
    }

    public PageBean<Goods> getGoodsPageBean(List<Goods> goodsList){
        //向下接口转型
        Page<Goods> page = (Page<Goods>) goodsList;
        PageBean<Goods> goodsPageBean = new PageBean<>();
        goodsPageBean.setItems(page.getResult());
        goodsPageBean.setTotal((int) page.getTotal());
        return goodsPageBean;
    }

    @Override
    public void addGoodsServer(Goods goods) {
        //随机生成id编号
        String id=null;
        Goods goodsById=new Goods();
        //一直生成。直到没有重复位ID为止
        while (goodsById!=null){
            id="gs"+UUID.randomUUID().toString().replace('-','x').substring(0,10);
            goodsById= goodsMapper.selectGoodsById(id);
        }
        goods.setStatus(0);
        goods.setId(id);
        goods.setUserId((String)((HashMap<String,Object>)ThreadLocalUtils.get()).get("id"));
        if(goods.getGoodsPic()==null){
            goods.setGoodsPic("");
        }
        goodsMapper.insertGoods(goods);

    }

    @Transactional
    @Override
    public void updateGoodsServer(Goods goods) {
        Goods goodsById = goodsMapper.selectGoodsById(goods.getId());
        if(goodsById==null){
            throw new RuntimeException("不存在该商品");
        }
        goodsMapper.updateGoods(goods);
        //尝试修改所在冷链室
       Integer role = (Integer) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("role");
        if(role==0&&!StringUtil.isNullOrEmpty(goods.getRmId())){

            //业务一：检查当前的农产品是否原来已经就在新改变的仓库中，如果存在就直接返回，如果不存在，就下一步
            Rm_Gs rmGs = new Rm_Gs();
            rmGs.setGdId(goods.getId());
            rmGs.setRmId(goods.getRmId());

            //从原来的RM_GS表的记录中，可以获取这个农产品对应的旧的冷链仓库Id
            Rm_Gs rmGsByOld = rmGsMapper.selectRm_GsByGoodsId(rmGs.getGdId());
            if(rmGsByOld.getRmId().equals(goods.getRmId())){
                return ;
            }

            //业务二：改变rm_gs表
            rmGsMapper.updateRmIdByGoodsId(rmGs);

            //业务三：减少原来的仓库的当前容量。。从上一个查到的rmgs对象中存有旧的冷链仓库Id，根据这个id来获取到旧的仓库对象
            //Refrigerate refrigerateOld = refrigerateMapper.selectRefrigerateById(rmGsByOld.getRmId());
            ////减少容量
            //refrigerateOld.setCurVolume(refrigerateOld.getCurVolume()-goods.getVolume());
            ////如果都被清空了，就设置为空闲
            //if(refrigerateOld.getCurVolume()<=0){
            //    refrigerateOld.setCurVolume(Float.valueOf(0));
            //    refrigerateOld.setStatus(1);
            //}
            ////执行更新冷链库表中旧的冷链库的当前容量或者状态，也可能需要更新
            //refrigerateMapper.updateRefrigerate(refrigerateOld);
            refrigerateServerImpl.changeRoomVolumeOperation(rmGsByOld.getRmId(),goods.getVolume(),0);


            //业务四：更新新仓库的容量。
            //Refrigerate refrigerateNew = refrigerateMapper.selectRefrigerateById(goods.getRmId());
            ////如果容量不够容下那么多东西
            //if(refrigerateNew.getMaxVolume()<refrigerateNew.getCurVolume()+goods.getVolume()){
            //    throw  new RuntimeException("该仓库容量不足，无法装载");//直接返回前端
            //}
            //Refrigerate refrigerate = new Refrigerate();
            //refrigerate.setCurVolume(refrigerateNew.getCurVolume()+goods.getVolume());
            //refrigerate.setId(goods.getRmId());
            //refrigerate.setStatus(2);
            ////更新
            //refrigerateMapper.updateRefrigerate(refrigerate);
            refrigerateServerImpl.changeRoomVolumeOperation(rmGs.getRmId(), goods.getVolume(), 1);
        }
    }

    @Override
    public void deleteGoodsById(String id) {
        Goods goods = goodsMapper.selectGoodsById(id);
        if(goods==null){
            throw new RuntimeException("不存在该农产品");
        }
        //查看该农产品是否存在运输子单
        Transportation TransportationByGoodsId = transportationMapper.selectTransportationByGoodsId(goods.getId());
        if(TransportationByGoodsId!=null){
            throw  new RuntimeException("该农产品存在其运输单号，不可删除");
        }
        //查看是否在冷藏当中，如果在，那就不能删除
        if(goods.getStatus()!=1){
            throw new RuntimeException("该农产品不在空闲状态，不能删除");
        }
        goodsMapper.deleteGoodsById(id);
    }

    @Override
    public Goods getGoodsById(String id) {
        return goodsMapper.detailGoodsByIdLinkRefrigerate(id);
    }

    /**
     *
     * @param goodsId 编号
     * @param volume 变化量
     * @param weight 变化量
     * @param type 0表示减少，1表示增加
     */
    @Transactional
    public void changeGoodsVolumeAndWeight(String goodsId,Float volume,Float weight,int type){
        //查出旧的goods
        Goods goods = goodsMapper.selectGoodsById(goodsId);
        if(type==0){
            goods.setVolume(goods.getVolume()-volume);
            goods.setWeight(goods.getWeight()-weight);
            if(goods.getWeight()==0){
                goods.setStatus(5);
            }
        }else{
            goods.setVolume(goods.getVolume()+volume);
            goods.setWeight(goods.getWeight()+weight);
            goods.setStatus(4);
        }
        //更新旧的
        goodsMapper.updateGoods(goods);
    }

}
