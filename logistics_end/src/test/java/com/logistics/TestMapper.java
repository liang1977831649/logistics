package com.logistics;

import com.logistics.mapper.AreaMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestMapper {
    @Autowired
    AreaMapper areaMapper;
    @Test
    public void test(){
        String areaCodeByName = areaMapper.getAreaCodeByName("田东县");
        System.out.println(areaCodeByName);
    }
}
