package com.logistics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String getHello(){
        System.out.println("<h1>hello</h1>");
        return "Hello";
    }
}
