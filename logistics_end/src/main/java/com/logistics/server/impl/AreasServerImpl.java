package com.logistics.server.impl;

import com.logistics.mapper.AreaMapper;
import com.logistics.server.AreasServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AreasServerImpl implements AreasServer {

    @Autowired
    private AreaMapper areaMapper;
    @Override
    public String getAreaCodeByName(String name) {
        return areaMapper.getAreaCodeByName(name);
    }

    @Override
    public List<String> getProvinceIdAndCityIdAndAreaIdByAreaId(String areaId) {
        String cityId = areaMapper.getCityIdByAreaId(areaId);
        String provinceId = areaMapper.getProvinceIdByCityId(cityId);
        List<String> strings = new ArrayList<>();
        strings.add(provinceId);
        strings.add(cityId);
        strings.add(areaId);

        return strings;
    }

}
