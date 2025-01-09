package com.logistics.server.impl;

import com.logistics.entity.Account;
import com.logistics.entity.ColdChainCenter;
import com.logistics.entity.User;
import com.logistics.mapper.AreaMapper;
import com.logistics.mapper.ColdChainCenterMapper;
import com.logistics.mapper.UserMapper;
import com.logistics.mapper.WebMapper;
import com.logistics.server.UserServer;
import com.logistics.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServerImpl implements UserServer {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private WebMapper webMapper;
    @Autowired
    private ColdChainCenterMapper coldChainCenterMapper;
    @Override
    public void insertUserForRegister(Account account) {

        //检查账号是否存在
        Account userById = webMapper.getUserById(account);
        if(userById!=null){
            throw new RuntimeException("该账号已存在");
        }

        User user=new User();
        user.setId(account.getId());
        user.setPassword(account.getPassword());
        user.setArea(account.getArea());
        user.setAreaId(account.getAreaId());

        user.setPassword(Md5Util.getMD5String(user.getPassword()));//设置密码

        user.setRole(1);//设置role

        /* String areaCode = areaMapper.getAreaCodeByName(user.getArea());//获取到地区的代码
        user.setAreaId(areaCode);//设置地区id */


        ColdChainCenter coldChainCenterByAreaId = coldChainCenterMapper.getColdChainCenterByAreaId(user.getAreaId());//尝试获取县级冷链中心
        //设置地区代码
        user.setCccId(coldChainCenterByAreaId==null?"0":coldChainCenterByAreaId.getId());

        userMapper.insertUserFroRegister(user);//插入
    }
}
