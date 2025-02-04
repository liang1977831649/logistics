package com.logistics.controller;

import com.logistics.entity.Account;
import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.entity.User;
import com.logistics.server.UserServer;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServer userServer;

    @GetMapping("/list")
    public Result getUserByAreaId(Integer pageNum,Integer pageSize,String name,String id){
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=8;
        }

        String areaId = (String)((HashMap<String, Object>) ThreadLocalUtils.get()).get("areaId");
        PageBean<User> userListByAreaId = userServer.getUserListByAreaId(pageNum, pageSize, areaId, name, id);
        return Result.success(userListByAreaId);
    }

    @PostMapping
    public Result add( @Validated(Account.Add.class) User user){
        //System.out.println("user==="+user);
        userServer.addUser(user);
        //System.out.println("user.getPassword()="+user.getPassword());
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody @Validated(Account.Update.class) User user){
        userServer.updateUser(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable String id){
        userServer.delete(id);
        return Result.success();
    }

    @PostMapping("/password")
    public Result updatePassword(String oldPwd,String newPwd,String rePwd){
        //获取到当前用户的id
        if(StringUtil.isNullOrEmpty(oldPwd)||StringUtil.isNullOrEmpty(newPwd)||StringUtil.isNullOrEmpty(rePwd)){
            return Result.error("数据格式错误");
        }
        if(!newPwd.equals(rePwd)){
            return Result.error("新密码和旧密码不一致");
        }
        String id =(String) ((HashMap<String,Object>)ThreadLocalUtils.get()).get("id");
        userServer.updatePassword(oldPwd, newPwd,id);

        return Result.success("成功！");
    }

    @GetMapping("/detail/{id}")
    public Result detailUser(@PathVariable String id){
        User user=userServer.detailUser(id);
        return Result.success(user);
    }

}
