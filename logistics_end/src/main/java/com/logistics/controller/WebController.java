package com.logistics.controller;

import com.logistics.entity.Account;
import com.logistics.entity.Result;
import com.logistics.mapper.AdminMapper;
import com.logistics.server.AdminServer;
import com.logistics.server.UserServer;
import com.logistics.server.WebServer;
import com.logistics.utils.JwtUtil;
import com.logistics.utils.Md5Util;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class WebController {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private WebServer webServer;
    @Autowired
    private UserServer userServer;
    @Autowired
    private AdminServer adminServer;


    @PostMapping("/login")
    public Result login(@Validated({Account.Login.class}) Account account){
        Account accountById =null;

        if(account.getRole()==0){//管理员
            accountById= webServer.adminLoginServer(account);
        }else if(account.getRole()==1){//普通用户
            accountById=webServer.userLoginServer(account);
        }else{
            return Result.error("暂不支持该方式登录");//司机
        }

        if(accountById==null){
            throw new RuntimeException("账号或密码错误");
        }

        account.setPassword(Md5Util.getMD5String(account.getPassword()));//md5设置密码
        if(!accountById.getPassword().equals (account.getPassword())){
            throw new RuntimeException("账号或密码错误");
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",accountById.getId());
        hashMap.put("role",accountById.getRole());
        hashMap.put("areaId",accountById.getAreaId());
        String token = JwtUtil.getToken(hashMap);

        //获取所在的地区：省份：市：县

        redisTemplate.opsForValue().set("token_"+account.getId(),token);//存储在redis
        return Result.success(token);
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
        if(role==0){
            account = adminServer.getAdminByIdNoPassword(accountId);
        }
        else if(role==1){
            account=userServer.getUserByIdNoPassword(accountId);
        }
        return Result.success(account);
    }

}
