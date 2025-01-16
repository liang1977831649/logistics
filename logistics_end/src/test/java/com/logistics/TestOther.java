package com.logistics;

import com.logistics.utils.DateCreateUtil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class TestOther {
    @Test
    public void date(){
        String s = DateCreateUtil.dateFileName();
        System.out.println(s);
    }
    @Test
    public void createNo(){
        String id= "gs"+UUID.randomUUID().toString().replace('-','x').substring(0,10);
        System.out.println(id);
    }
}
