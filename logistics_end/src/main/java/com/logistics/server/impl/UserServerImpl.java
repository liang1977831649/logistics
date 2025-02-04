package com.logistics.server.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.logistics.entity.*;
import com.logistics.mapper.AreaMapper;
import com.logistics.mapper.UserMapper;
import com.logistics.mapper.WebMapper;
import com.logistics.server.UserServer;
import com.logistics.utils.Md5Util;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class UserServerImpl implements UserServer {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private WebMapper webMapper;

    @Override
    public void insertUserForRegister(Account account) {

        //检查账号是否存在
        Account userById = webMapper.getUserById(account);
        if (userById != null) {
            throw new RuntimeException("该账号已存在");
        }

        User user = new User();
        user.setId(account.getId());
        user.setPassword(account.getPassword());
        user.setArea(account.getArea());
        user.setAreaId(account.getAreaId());

        user.setPassword(Md5Util.getMD5String(user.getPassword()));//设置密码

        user.setRole(1);//设置role
        user.setName("HelloWorld");

        /* String areaCode = areaMapper.getAreaCodeByName(user.getArea());//获取到地区的代码
        user.setAreaId(areaCode);//设置地区id */


        //ColdChainCenter coldChainCenterByAreaId = coldChainCenterMapper.getColdChainCenterByAreaId(user.getAreaId());//尝试获取县级冷链中心
        //设置地区代码
        //user.setCccId(coldChainCenterByAreaId==null?"0":coldChainCenterByAreaId.getId());

        userMapper.insertUserFroRegister(user);//插入
    }


    @Override
    public PageBean<User> getUserListByAreaId(Integer pageNum, Integer pageSize, String areaId, String name, String id) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.selectUserByAreaId(areaId, name, id);

        //设置地域名
        String areaCodeByName = areaMapper.getAreaNameByAreaCode(areaId);
        for (User user : users) {
            user.setArea(areaCodeByName);
        }
        //向下接口转型
        Page<User> page = (Page<User>) users;
        PageBean<User> userPageBean = new PageBean<>();
        userPageBean.setItems(page.getResult());
        userPageBean.setTotal((int) page.getTotal());

        return userPageBean;
    }

    @Override
    public void addUser(User user) {
        //账号是否重复
        Account account = new Account();
        account.setId(user.getId());
        User userById = webMapper.getUserById(account);
        if (userById != null) {
            throw new RuntimeException("该账号已存在");
        }
        //设置一些字段
        user.setRole(1);
        if(user.getPhone()==null){
            user.setPhone("");
        }
        if(user.getUserPic()==null){
            user.setUserPic("");
        }
        String areaId = (String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        user.setAreaId(areaId);
        user.setPassword(Md5Util.getMD5String(user.getPassword()));
        userMapper.insertUserForAdd(user);
    }

    @Override
    public void updateUser(User user) {
        //验证账号是否存在
        Account account = new Account();
        account.setId(user.getId());
        User userById = webMapper.getUserById(account);
        if(userById==null){
            throw  new RuntimeException("该账户不存在");
        }
        if(!StringUtil.isNullOrEmpty(user.getPassword())){
            user.setPassword(Md5Util.getMD5String(user.getPassword()));
        }
        userMapper.update(user);
    }

    @Override
    public User getUserByIdNoPassword(String id) {
        return userMapper.getUserByIdNoPassword(id);
    }

    @Override
    public void delete(String id) {
        userMapper.deleteUserById(id);
    }

    @Override
    public void updatePassword(String oldPwd, String newPwd, String id) {
        //查看原密码是否正确
        Account account = new Account();
        account.setId(id);
        User user = webMapper.getUserById(account);
        if (user == null) {
            throw new RuntimeException("该账号不存在");
        }
        //加密原密码
        oldPwd = Md5Util.getMD5String(oldPwd);
        if (!oldPwd.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        newPwd = Md5Util.getMD5String(newPwd);
        userMapper.updateUserPassword(newPwd, id);
    }

    @Override
    public User detailUser(String id) {
        User user = userMapper.selectUserById(id);
        return user;
    }
}
