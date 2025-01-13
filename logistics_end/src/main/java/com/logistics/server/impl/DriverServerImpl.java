package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.Driver;
import com.logistics.entity.PageBean;
import com.logistics.entity.User;
import com.logistics.mapper.AreaMapper;
import com.logistics.mapper.DriverMapper;
import com.logistics.server.DriverServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class DriverServerImpl implements DriverServer {
    @Autowired
    private DriverMapper driverMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Override
    public PageBean<Driver> getUserListByAreaId(Integer pageNum, Integer pageSize, String areaId, String name, String id,Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Driver> drivers = driverMapper.selectDriverByAreaId(areaId, name, id,status);

        //设置地域名
        String areaCodeByName = areaMapper.getAreaNameByAreaCode(areaId);
        for (Driver driver : drivers) {
            driver.setArea(areaCodeByName);
        }
        //向下接口转型
        Page<Driver> page = (Page<Driver>) drivers;
        PageBean<Driver> userPageBean = new PageBean<>();
        userPageBean.setItems(page.getResult());
        userPageBean.setTotal((int) page.getTotal());

        return userPageBean;
    }

    @Override
    public void updateDriver(Driver driver) {
        //查找是否有这个driver
        Driver driverById = driverMapper.selectDriverById(driver.getId());
        if(driverById==null){
            throw new RuntimeException("不存在该司机");
        }
        driverMapper.updateDriver(driver);
    }

    @Override
    public void addDriver(Driver driver) {
        Driver driverById = driverMapper.selectDriverById(driver.getId());
        if(driverById!=null){
            throw new RuntimeException("该司机已存在");
        }
        String areaId=(String)((HashMap<String,Object>)ThreadLocalUtils.get()).get("areaId");
        driver.setAreaId(areaId);
        //刚添加进来的状态应该是空闲的
        driver.setStatus(1);
        driverMapper.insertDriver(driver);
    }

    @Override
    public void deleteDriverById(String id) {
        driverMapper.deleteById(id);
    }
}
