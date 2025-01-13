package com.logistics.server;

import com.logistics.entity.Driver;
import com.logistics.entity.PageBean;
import com.logistics.entity.User;

public interface DriverServer {
    PageBean<Driver> getUserListByAreaId(Integer pageNum, Integer pageSize, String areaId, String name, String id,Integer status);

    void updateDriver(Driver driver);

    void addDriver(Driver driver);

    void deleteDriverById(String id);
}
