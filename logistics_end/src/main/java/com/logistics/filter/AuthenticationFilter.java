package com.logistics.filter;

import com.logistics.entity.Account;
import com.logistics.utils.JwtUtil;
import com.logistics.utils.RedisCache;
import com.logistics.utils.ThreadLocalUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;

    //过滤器发生异常，由我们自己抛出去
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        List<String> permitPath=new ArrayList<>(Arrays.asList("/login","/register"));
        System.out.println(request.getServletPath());
        HashMap<String, Object> hashMap = new HashMap<>();
        if (permitPath.contains(request.getServletPath())) {
            //如果是空的情况。那就证明只能是登录、注册
            //判断是普通用户还是，
            String role = request.getParameter("role");
            hashMap.put("role",role);
            ThreadLocalUtils.set(hashMap);
            filterChain.doFilter(request, response);
            return;
        }
        //如果是忘记密码；
        if(request.getServletPath().contains("/getPhone")){
            filterChain.doFilter(request,response);
            return;
        }
        //那其他就是访问功能接口
        //检验JWT
        String id;
        try {
            id = JwtUtil.parseTokenForString(token);
        } catch (Exception e) {
            response.setStatus(401);
            resolver.resolveException(request,response,
                    null,new RuntimeException("登陆已过期"));
            return;
        }
        Account account = redisCache.getCacheObject("obj_" + id);
        hashMap.put("id",id);
        hashMap.put("role",account.getRole());
        hashMap.put("areaId",account.getAreaId());
        //存进Hash Map中
        ThreadLocalUtils.set(hashMap);

        //匿名访问不允许登陆过的用户来访问接口
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
