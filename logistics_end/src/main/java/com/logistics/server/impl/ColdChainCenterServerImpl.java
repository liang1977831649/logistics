package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.ColdChainCar;
import com.logistics.entity.ColdChainCenter;
import com.logistics.entity.PageBean;
import com.logistics.entity.Refrigerate;
import com.logistics.mapper.ColdChainCenterMapper;
import com.logistics.mapper.RefrigerateMapper;
import com.logistics.server.ColdChainCenterServer;
import com.logistics.utils.ThreadLocalUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;

@Service
public class ColdChainCenterServerImpl implements ColdChainCenterServer {

    @Autowired
    ColdChainCenterMapper coldChainCenterMapper;
    @Autowired
    RefrigerateMapper refrigerateMapper;

    public List<ColdChainCenter> getColdChainCenterByAreaId(String areaId) {
        return coldChainCenterMapper.getColdChainCenterByAreaId(areaId);
    }

    @Override
    public PageBean<ColdChainCenter> getColdChainCenterByAreaId(Integer pageNum, Integer pageSize, String name, String id) {
        PageHelper.startPage(pageNum, pageSize);
        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        List<ColdChainCenter> coldChainCenters = coldChainCenterMapper.selectColdChainCenterByAreaId(id, name, areaId);

        Page<ColdChainCenter> page = (Page<ColdChainCenter>) coldChainCenters;
        PageBean<ColdChainCenter> pageBean = new PageBean<>();
        pageBean.setItems(page.getResult());
        pageBean.setTotal((int) page.getTotal());
        return pageBean;
    }

    @Override
    public void addColdCenter(ColdChainCenter coldChainCenter) {
        ColdChainCenter coldChainCenterById = coldChainCenterMapper.selectColdChainCenterById(coldChainCenter.getId());
        if (coldChainCenterById != null) {
            throw new RuntimeException("已经存在该Id的冷链中新");
        }
        String areaId = (String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        coldChainCenter.setAreaId(areaId);
        coldChainCenterMapper.addColdChainServer(coldChainCenter);
    }

    @Override
    public void updateColdChainCenter(ColdChainCenter coldChainCenter) {
        ColdChainCenter coldChainCenterById = coldChainCenterMapper.selectColdChainCenterById(coldChainCenter.getId());
        if (coldChainCenterById == null) {
            throw new RuntimeException("不存在该Id的冷链中新");
        }
        coldChainCenterMapper.updateColdChainCenter(coldChainCenter);
    }

    @Override
    public void deleteColdChainCenter(String id) {
        ColdChainCenter coldChainCenterById = coldChainCenterMapper.selectColdChainCenterById(id);
        if (coldChainCenterById == null) {
            throw new RuntimeException("不存在该Id的冷链中新");
        }
        List<Refrigerate> refrigerates = refrigerateMapper.selectRefrigerateByCccId(coldChainCenterById.getId());
        if(!refrigerates.isEmpty()){
            throw new RuntimeException("该冷链中心有冷链室，不可删除");
        }
        coldChainCenterMapper.deleteColdChainCenter(id);
    }


}
