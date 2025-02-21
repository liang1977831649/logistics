package com.logistics.controller;

import com.logistics.entity.Admin;
import com.logistics.entity.PageBean;
import com.logistics.entity.Result;
import com.logistics.server.AdminServer;
import com.logistics.utils.ThreadLocalUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminServer adminServer;

    @PostMapping("/password")
    @PreAuthorize("@ex.verificationHandler(0)")
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
    @PutMapping
    @PreAuthorize("@ex.verificationHandler(2)")
    public Result updateInfo(Admin admin){
        adminServer.updateAdmin(admin);
        return Result.success();
    }
    @GetMapping("/list")
    @PreAuthorize("@ex.verificationHandler(2)")
    public Result list(Integer pageNum,Integer pageSize,String name,String id){
        if(pageNum==null){
            pageNum=1;
        }
        if(pageSize==null){
            pageSize=8;
        }
        PageBean<Admin> adminList = adminServer.getAdminList(pageNum, pageSize, name, id);
        return Result.success(adminList);
    }
    @PostMapping
    @PreAuthorize("@ex.verificationHandler(2)")
    public Result addAdmin(@RequestBody @Validated(Admin.Add.class) Admin admin){
        adminServer.addAdmin(admin);
        return Result.success();
    }
    @PutMapping("/updateByManager")
    @PreAuthorize("hasAnyAuthority(2,0)")
    public Result updateAdmin(@RequestBody Admin admin){
        adminServer.updateAdminByManager(admin);
        return Result.success();
    }
}
