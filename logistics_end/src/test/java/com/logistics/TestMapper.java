package com.logistics;

import com.logistics.entity.Account;
import com.logistics.entity.Admin;
import com.logistics.entity.User;
import com.logistics.mapper.AdminMapper;
import com.logistics.mapper.AreaMapper;
import com.logistics.mapper.UserMapper;
import com.logistics.mapper.WebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestMapper {
    @Autowired
    AreaMapper areaMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    AdminMapper adminMapper;
    @Autowired
    WebMapper webMapper;
    @Test
    public void test(){
        String areaCodeByName = areaMapper.getAreaCodeByName("田东县");
        System.out.println(areaCodeByName);
    }

    @Test
    public void test2(){
        List<User> users = userMapper.selectUserByAreaId("451022", "", "654516");
        System.out.println(users);
    }
    @Test
    public void test3(){
        Account account = new Account();
        account.setId("123456");
        Admin adminById = webMapper.getAdminById(account);
        System.out.println(adminById.getAreaId());
    }
    @Test
    public void test4(){


    }
}
