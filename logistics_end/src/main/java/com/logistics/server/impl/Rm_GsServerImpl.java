package com.logistics.server.impl;

import com.logistics.entity.Rm_Gs;
import com.logistics.mapper.Rm_GsMapper;
import com.logistics.server.Rm_GsServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Rm_GsServerImpl implements Rm_GsServer {
    @Autowired
    private Rm_GsMapper rmGsMapper;
    @Override
    public Rm_Gs getRm_GsByGoodsId(String id){
        return rmGsMapper.selectRm_GsByGoodsId(id);
    }
}
