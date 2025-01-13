package com.logistics;

import com.logistics.entity.ColdChainCenter;
import com.logistics.entity.PageBean;
import com.logistics.entity.User;
import com.logistics.server.AreasServer;
import com.logistics.server.ColdChainCenterServer;
import com.logistics.server.UserServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestServer {
    @Autowired
    ColdChainCenterServer coldChainCenterServer;

    @Autowired
    AreasServer areasServer;
    @Autowired
    UserServer  userServer;
    @Test
    public void test1(){
        ColdChainCenter coldChainCenterByAreaId = coldChainCenterServer.getColdChainCenterByAreaId("451022");
        System.out.println(coldChainCenterByAreaId);
    }

    @Test
    public void test2(){
        PageBean<User> userListByAreaId = userServer.getUserListByAreaId(1, 5, "451022", null, null);
        System.out.println(userListByAreaId);
    }
    @Test
    public void test3(){
        List<String> provinceIdAndCityIdAndAreaIdByAreaId = areasServer.getProvinceIdAndCityIdAndAreaIdByAreaId("451022");
        System.out.println(provinceIdAndCityIdAndAreaIdByAreaId);
    }
    @Test
    public void test4(){
        PageBean<User> userListByAreaId = userServer.getUserListByAreaId(1, 8, "451022", "", "");
        System.out.println(userListByAreaId);
    }
    @Test
    public void test5(){
        User user = new User();
        user.setId("2021114");
        user.setPassword("654321");
        userServer.updateUser(user);
    }
}
