package com.logistics.controller;

import com.logistics.entity.Account;
import com.logistics.entity.Money;
import com.logistics.entity.Result;
import com.logistics.entity.User;
import com.logistics.server.MoneyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/money")
public class MoneyController {
    @Autowired
    private MoneyServer moneyServer;
    @GetMapping("/get")
    public Result getMoney(){
        Money money  = moneyServer.getMoneyByUserId();
        return Result.success(money);
    }
    @PutMapping
    public Result updateMoneyByUserId( @RequestBody Money money){
        moneyServer.updateMoneyByUserId(money);
        return Result.success();
    }
    @PostMapping("/passwordValid")
    public Result passwordValid(@RequestBody Account account){
        if(account==null||account.getPassword()==null||account.getPassword().isEmpty()){
            return Result.error("请输入密码");
        }
        moneyServer.passwordValid(account);
        return Result.success();
    }

    @PostMapping
    public Result addMoneyAccount(){
        moneyServer.addMoneyAccount();
        return Result.success();
    }
    @GetMapping("/withdraw")
    public Result withdrawMoney(){
        moneyServer.withdrawMoney();
        return Result.success();
    }
}
