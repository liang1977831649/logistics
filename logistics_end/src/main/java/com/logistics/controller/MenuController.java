package com.logistics.controller;

import com.logistics.entity.Menu;
import com.logistics.entity.Result;
import com.logistics.server.MenuServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MenuController {
    @Autowired
    MenuServer menuServer;

    @GetMapping("/menu")
    public Result getMenuList(){
        List<Menu> menuListByRole = menuServer.getMenuListByRole();
        return Result.success(menuListByRole);
    }
}
