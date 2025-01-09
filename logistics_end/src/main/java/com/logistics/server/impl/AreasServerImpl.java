package com.logistics.server.impl;

import com.logistics.mapper.AreaMapper;
import com.logistics.server.AreasServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AreasServerImpl implements AreasServer {

    @Autowired
    private AreaMapper areaMapper;
    @Override
    public String getAreaCodeByName(String name) {
        return areaMapper.getAreaCodeByName(name);
    }
}
