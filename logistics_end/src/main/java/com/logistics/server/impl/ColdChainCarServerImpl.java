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
        List<ColdChainCar> coldChainCars = coldChainCarMapper.selectColdChainCarByAreaId(areaId, name, id,status);

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
        if(coldChainCarById==null){
            throw new RuntimeException("不存在该冷链车");
        }

        //一旦处于空闲状态，那么温湿度，都会被位置为0
        if(coldChainCar.getStatus()==1){
            coldChainCar.setHum(0);
            coldChainCar.setTem(0);
        }
        coldChainCarMapper.updateDriver(coldChainCar);
    }

    @Override
    public void addColdChainCar(ColdChainCar coldChainCar) {
        ColdChainCar coldChainCarById = coldChainCarMapper.selectColdChainCarById(coldChainCar.getId());
        if(coldChainCarById!=null){
            throw new RuntimeException("已存在该卡车");
        }
        String areaId = (String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        coldChainCar.setAreaId(areaId);
        coldChainCar.setStatus(1);
        coldChainCarMapper.addColdChainCa(coldChainCar);
    }

    @Override
    public void deleteColdChainCarById(String id) {
        coldChainCarMapper.deleteColdChainCarById(id);
    }
}
