package com.logistics.server.impl;

import com.logistics.entity.Account;
import com.logistics.entity.Admin;
import com.logistics.entity.User;
import com.logistics.mapper.WebMapper;
import com.logistics.server.WebServer;

import com.logistics.utils.JwtUtil;
import com.logistics.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class WebServerImpl implements WebServer {
    @Autowired
    private WebMapper webMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Account adminLoginServer(Account account) {
        return webMapper.getAdminById(account);
    }

    @Override
    public Account userLoginServer(Account account) {
        return webMapper.getUserById(account);
    }

    @Override
    public Admin getAdminById(String id) {
        Account account = new Account();
        account.setId(id);
        return webMapper.getAdminById(account);
    }

    @Override
    public User getUserById(String id) {
        Account account = new Account();
        account.setId(id);
        return webMapper.getUserById(account);
    }

    @Override
    public String login(Account account) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(account.getId(),account.getPassword(),account.getAuthorities());
        //下面这一条语句它不仅仅是检验账号，他还有校验密码，但是不会经过security 的过滤器链
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        //获取到对象
        Account accountByPrincipal =(Account) authenticate.getPrincipal();
        if(accountByPrincipal==null){
            throw new RuntimeException("账号或密码错误");
        }
        String id = accountByPrincipal.getId();
        String jwt = JwtUtil.getTokenForString(id);
        redisCache.setCacheObject("obj_"+id,accountByPrincipal);
        return jwt;
    }
}
