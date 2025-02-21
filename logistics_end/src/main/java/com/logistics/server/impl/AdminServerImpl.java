package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.AdminMapper;
import com.logistics.mapper.WebMapper;
import com.logistics.server.AdminServer;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServerImpl implements AdminServer {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private WebMapper webMapper;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    public Admin getAdminByIdNoPassword(String id) {
        return adminMapper.getAdminByIdNoPassword(id);
    }

    @Override
    public void updatePasswordByAdminId(String oldPwd, String newPwd, String id) {
        //查看原密码是否正确
        Account account = new Account();
        account.setId(id);
        Admin adminById = webMapper.getAdminById(account);
        if (adminById == null) {
            throw new RuntimeException("该账号不存在");
        }
        //加密原密码
        if(!bCryptPasswordEncoder.matches(oldPwd,adminById.getPassword())){
            throw new RuntimeException("原密码错误");
        }
        newPwd = bCryptPasswordEncoder.encode(newPwd);
        adminMapper.updateAdminPassword(newPwd, id);
    }

    @Override
    public void updateAdmin(Admin admin) {
        adminMapper.updateAdmin(admin);
    }

    @Override
    public String selectAdminPhone(String areaId) {
        Admin admin = adminMapper.selectAdminByAreaId(areaId);
        if(admin==null|| StringUtil.isNullOrEmpty(admin.getPhone())){
            throw new RuntimeException("暂无联系方式");
        }
        return admin.getPhone();
    }

    @Override
    public  PageBean<Admin> getAdminList(Integer pageNum, Integer pageSize, String name, String id) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> admins = adminMapper.selectAdminList(name, id);

        //向下接口转型
        Page<Admin> page = (Page<Admin>) admins;
        PageBean<Admin> adminPageBean = new PageBean<>();
        adminPageBean.setItems(page.getResult());
        adminPageBean.setTotal((int) page.getTotal());

        return adminPageBean;
    }

    @Override
    public void addAdmin(Admin admin) {
        Account account = new Account();
        account.setId(admin.getId());
        Admin adminById = webMapper.getAdminById(account);
        if(adminById!=null){
            throw new RuntimeException("已存在该Id的管理员");
        }
        admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
        adminMapper.insertAdmin(admin);
    }

    @Override
    public void updateAdminByManager(Admin admin) {
        Account account = new Account();
        account.setId(admin.getId());
        if(webMapper.getAdminById(account)==null){
            throw new RuntimeException("该管理员不存在");
        }
        if(!StringUtil.isNullOrEmpty(admin.getPassword())){
            admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
        }
        adminMapper.updateAdminByManager(admin);
    }
}
