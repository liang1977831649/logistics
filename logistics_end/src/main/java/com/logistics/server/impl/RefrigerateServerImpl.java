package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.PageBean;
import com.logistics.entity.Refrigerate;
import com.logistics.mapper.RefrigerateMapper;
import com.logistics.server.RefrigerateServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefrigerateServerImpl implements RefrigerateServer {
    @Autowired
    private RefrigerateMapper refrigerateMapper;
    @Override
    public PageBean<Refrigerate> getRefrigerateListByAreaId(Integer pageNum, Integer pageSize, String areaId, String name, String id, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Refrigerate> refrigerate = refrigerateMapper.selectRefrigerateByAreaId(areaId, name, id,status);
        //向下接口转型
        Page<Refrigerate> page = (Page<Refrigerate>) refrigerate;
        PageBean<Refrigerate> userPageBean = new PageBean<>();
        userPageBean.setItems(page.getResult());
        userPageBean.setTotal((int) page.getTotal());
        return userPageBean;
    }

    @Override
    public void updateRefrigerate(Refrigerate refrigerate) {
        Refrigerate refrigerateById = refrigerateMapper.selectRefrigerateById(refrigerate.getId());
        if(refrigerateById==null){
            throw new RuntimeException("不存在该Id的冷链室");
        }
        refrigerateMapper.updateRefrigerate(refrigerate);
    }

    @Override
    public void addRefrigerate(Refrigerate refrigerate) {
        Refrigerate refrigerateById = refrigerateMapper.selectRefrigerateById(refrigerate.getId());
        if(refrigerateById!=null){
            throw new RuntimeException("已存在该Id的冷链室");
        }
        refrigerateMapper.insertAddRefrigerate(refrigerate);
    }

    @Override
    public void deleteRefrigerateById(String id) {
        Refrigerate refrigerateById = refrigerateMapper.selectRefrigerateById(id);
        if(refrigerateById==null){
            throw new RuntimeException("不存在该Id的冷链室,不能删除");
        }
        if(refrigerateById.getStatus()!=1){
            throw new RuntimeException("该冷链室有冷链品，不可删除");
        }
        refrigerateMapper.deleteRefrigerateById(id);
    }

    @Override
    public Refrigerate getRefrigerateById(String id) {
        return refrigerateMapper.selectRefrigerateById(id);
    }
}
