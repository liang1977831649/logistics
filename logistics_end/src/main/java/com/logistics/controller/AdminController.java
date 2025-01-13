package com.logistics.controller;

import com.logistics.entity.Result;
import com.logistics.server.AdminServer;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminServer adminServer;
    @PostMapping("/password")
    public Result updatePassword(String oldPwd,String newPwd,String rePwd){
        if(StringUtil.isNullOrEmpty(oldPwd)||StringUtil.isNullOrEmpty(newPwd)||StringUtil.isNullOrEmpty(rePwd)){
            return Result.error("数据格式错误");
        }
        if(!newPwd.equals(rePwd)){
            return Result.error("新密码和重复密码不一致");
        }
        String id =(String) ((HashMap<String,Object>)ThreadLocalUtils.get()).get("id");
        adminServer.updatePasswordByAdminId(oldPwd,newPwd,id);
        return Result.success();
    }
}
