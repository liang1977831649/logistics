package com.logistics.controller;

import com.logistics.entity.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/hello")
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result hello(){
        System.out.println("Hello");
        return Result.success("你好");
    }
    @RequestMapping("/world")
    @PreAuthorize("@ex.verificationHandler(0)")
    public Result world(){
        System.out.println("Hello");
        return Result.success("世界");
    }
    @RequestMapping("/java")
    @PreAuthorize("@ex.verificationHandler(1)")
    public Result java(){
        System.out.println("Hello");
        return Result.success("java");
    }
}
