package com.logistics.server.impl;

import com.logistics.entity.Account;
import com.logistics.entity.Admin;
import com.logistics.mapper.WebMapper;
import com.logistics.server.WebLoginServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class WebLoginServerImpl implements WebLoginServer, UserDetailsService {
    @Autowired
    WebMapper webMapper;
    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        Integer role =Integer.parseInt((String) ((HashMap<String, Object>) ThreadLocalUtils.get()).get("role"));
        Account account = new Account();
        account.setId(accountId);
        if(role==0||role==2){
            //管理员和超管
            account = webMapper.getAdminById(account);
        }else{
            //普通用户
            account= webMapper.getUserById(account);
        }
        if(account==null){
            throw new RuntimeException("账号或密码错误");
        }
        return account;
    }
}
