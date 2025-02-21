package com.logistics;

import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.server.DeliveryServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
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

    @Autowired
    TransportationMapper transportationMapper;
    @Autowired
    DeliveryMapper deliveryMapper;

    @Autowired
    MoneyMapper moneyMapper;
    @Autowired
    GoodsCostMapper goodsCostMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;


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
        if(passwordEncoder.matches("1234565","$2a$10$Nl0K1FlfaJXACGuDrnTWrecqmvB0b3ShFhITye57pFdRQXlL2urre")){
            System.out.println("密码正确");
        }
    }
    @Test
    public void test4(){
        Delivery delivery = deliveryMapper.selectDeliveryById("dec1217ecex0");
        delivery.setCarDriId("1344fwedqw2");
        deliveryMapper.updateDelivery(delivery);
    }
    @Test
    public void test5(){
        Delivery delivery = new Delivery();
        delivery.setId("de6a2e02f4xb");
        delivery.setDepartTime(LocalDateTime.now());
        deliveryMapper.updateDelivery(delivery);
    }

    @Test
    public void test6(){
        List<GoodsCost> goodsCosts = goodsCostMapper.selectGoodsCostListByUserId(null, null, null, null, "202168484",1);
        System.out.println(goodsCosts);
    }

    @Test
    public void test7(){
        List<User> users = userMapper.selectUserByAreaId("451022", null, null);
        for (User user : users) {
            //moneyMapper.insertAccountForUser(user.getId());
        }
    }

}
