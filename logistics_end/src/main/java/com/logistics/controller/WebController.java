package com.logistics.controller;

import com.logistics.entity.Account;
import com.logistics.entity.Result;
import com.logistics.server.AdminServer;
import com.logistics.server.UserServer;
import com.logistics.server.WebServer;
import com.logistics.utils.RedisCache;
import com.logistics.utils.ThreadLocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class WebController {

    @Autowired
    private WebServer webServer;
    @Autowired
    private UserServer userServer;
    @Autowired
    private AdminServer adminServer;
    @Autowired
    private RedisCache redisCache;

    @PostMapping("/login")
    public Result login(@Validated({Account.Login.class})Account account){
        String jwt = webServer.login(account);
        return Result.success(jwt);
    }
    @PostMapping("/register")
    public Result registerUser(@Validated(Account.Register.class) Account account){
        if(account.getRole()!=null){
            throw new RuntimeException("普通用户不能注册管理员");
        }
        userServer.insertUserForRegister(account);
        return Result.success();
    }

    @GetMapping("/getPersonInfo")
    public Result getAccountInfo(){
        HashMap<String, Object> hashMap= ThreadLocalUtils.get();
        String  accountId = (String) hashMap.get("id");
        Integer role = (Integer) hashMap.get("role");
        Account account=null;
        if(role==0||role==2){
            account = adminServer.getAdminByIdNoPassword(accountId);
        }
        else if(role==1){
            account=userServer.getUserByIdNoPassword(accountId);
        }
        return Result.success(account);
    }
    @GetMapping("/getPhone/{areaId}")
    public Result getAdminPhone( @PathVariable String areaId){
        String phone = adminServer.selectAdminPhone(areaId);
        return Result.success(phone);
    }
    @PostMapping("/loginOut")
    public Result loginOut(HttpServletRequest request, HttpServletResponse response){
        //System.out.println("helloWorld");
        //String token = request.getHeader("Authorization");
        //从容器中获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Account loginUser = (Account) authentication.getPrincipal();
        String  id=(String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("id");

        //清除security
        if(loginUser!=null){
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }
        //清除Redis信息
        redisCache.deleteObject("obj_"+id);
        return Result.success("退出成功");
    }
}
