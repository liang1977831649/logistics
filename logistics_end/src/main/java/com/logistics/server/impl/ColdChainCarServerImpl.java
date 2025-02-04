package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.ColdChainCar;
import com.logistics.entity.Driver;
import com.logistics.entity.PageBean;
import com.logistics.mapper.AreaMapper;
import com.logistics.mapper.ColdChainCarMapper;
import com.logistics.server.ColdChainCarServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
public class ColdChainCarServerImpl implements ColdChainCarServer {
    @Autowired
    private ColdChainCarMapper coldChainCarMapper;
    @Autowired
    private AreaMapper areaMapper;

    @Override
    public PageBean<ColdChainCar> getColdChainCarListByAreaId(Integer pageNum, Integer pageSize, String areaId, String name, String id, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<ColdChainCar> coldChainCars = coldChainCarMapper.selectColdChainCarByAreaId(areaId, name, id, status);

        //设置地域名
        String areaCodeByName = areaMapper.getAreaNameByAreaCode(areaId);
        for (ColdChainCar coldChainCar : coldChainCars) {
            coldChainCar.setArea(areaCodeByName);
        }
        //向下接口转型
        Page<ColdChainCar> page = (Page<ColdChainCar>) coldChainCars;
        PageBean<ColdChainCar> userPageBean = new PageBean<>();
        userPageBean.setItems(page.getResult());
        userPageBean.setTotal((int) page.getTotal());
        return userPageBean;
    }

    @Override
    public void updateColdChainCar(ColdChainCar coldChainCar) {
        //查找是否有这个driver
        ColdChainCar coldChainCarById = coldChainCarMapper.selectColdChainCarById(coldChainCar.getId());
        if (coldChainCarById == null) {
            throw new RuntimeException("不存在该冷链车");
        }


        coldChainCarMapper.updateCar(coldChainCar);
    }

    @Override
    public void addColdChainCar(ColdChainCar coldChainCar) {
        ColdChainCar coldChainCarById = coldChainCarMapper.selectColdChainCarById(coldChainCar.getId());
        if (coldChainCarById != null) {
            throw new RuntimeException("已存在该卡车");
        }
        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        coldChainCar.setAreaId(areaId);
        coldChainCarMapper.addColdChainCa(coldChainCar);
    }

    @Override
    public void deleteColdChainCarById(String id) {
        ColdChainCar coldChainCarById = coldChainCarMapper.selectColdChainCarById(id);
        if (coldChainCarById.getStatus() != 1) {
            throw new RuntimeException("该冷链车状态不为空闲，不可删除");
        }
        coldChainCarMapper.deleteColdChainCarById(id);
    }

    @Override
    public ColdChainCar detailCar(String carId) {
        ColdChainCar coldChainCarById = coldChainCarMapper.selectColdChainCarById(carId);
        if (coldChainCarById == null) {
            throw new RuntimeException("没有该司机");
        }
        return coldChainCarById;
    }

    /**
     *
     * @param carId 车辆Id
     * @param volume 变化量
     * @param weight 变化量
     * @param type 类型：0减少，1增加
     */
    @Transactional
    public void changeCarWeightAndWeight(String carId,Float volume,Float weight,int type){
        ColdChainCar coldChainCarById = coldChainCarMapper.selectColdChainCarById(carId);

        Float volumeNew;
        Float weightNew;
        if(type==1){
            volumeNew=volume+coldChainCarById.getCurVolume();
            weightNew=weight+coldChainCarById.getCurWeight();
            if(volumeNew>coldChainCarById.getVolume()||weightNew>coldChainCarById.getWeight()){
                throw new RuntimeException("超载或超空间，不可装货");
            }
        }else{
            volumeNew=coldChainCarById.getCurVolume()-volume;
            weightNew=coldChainCarById.getCurWeight()-weight;
        }
        coldChainCarById.setCurWeight(weightNew);
        coldChainCarById.setCurVolume(volumeNew);
        coldChainCarMapper.updateCar(coldChainCarById);
    }
}
