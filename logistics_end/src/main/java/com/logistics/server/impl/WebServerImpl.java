package com.logistics.server.impl;

import com.logistics.entity.Account;
import com.logistics.entity.Admin;
import com.logistics.entity.User;
import com.logistics.mapper.WebMapper;
import com.logistics.server.WebServer;
import com.logistics.utils.JwtUtil;
import com.logistics.utils.Md5Util;
import com.logistics.utils.ThreadLocalUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class WebServerImpl implements WebServer {
    @Autowired
    private WebMapper webMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public Account adminLoginServer(Account account) {
        return webMapper.getAdminById(account);
    }

    @Override
    public Account userLoginServer(Account account) {
        return webMapper.getUserById(account);
    }
}
