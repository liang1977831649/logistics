package com.logistics.server.impl;

import com.logistics.entity.Account;
import com.logistics.entity.Admin;
import com.logistics.mapper.AdminMapper;
import com.logistics.mapper.WebMapper;
import com.logistics.server.AdminServer;
import com.logistics.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServerImpl implements AdminServer {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private WebMapper webMapper;

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
        oldPwd = Md5Util.getMD5String(oldPwd);
        if (!oldPwd.equals(adminById.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        newPwd = Md5Util.getMD5String(newPwd);
        adminMapper.updateAdminPassword(newPwd, id);
    }
}
