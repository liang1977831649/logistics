package com.logistics.interceptor;

import com.logistics.utils.JwtUtil;
import com.logistics.utils.ThreadLocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;


@Component
public class LoginAndRegisterInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        System.out.println("token=" + authorization);
        HashMap<String, Object> hashMap = null;
        try {
            hashMap = (HashMap<String, Object>) JwtUtil.parseToken(authorization);
            String redisToken = redisTemplate.opsForValue().get("token_" + hashMap.get("id"));
            if(!redisToken.equals(authorization)){
                response.setStatus(401);
                throw new RuntimeException();
            }

            ThreadLocalUtils.set(hashMap);
        } catch (Exception e) {
            response.setStatus(401);
            throw new RuntimeException(e);
        }
        return true;
    }

}
