package com.logistics;

import com.logistics.entity.ColdChainCenter;
import com.logistics.server.ColdChainCenterServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestServer {
    @Autowired
    ColdChainCenterServer coldChainCenterServer;

    @Test
    public void test1(){
        ColdChainCenter coldChainCenterByAreaId = coldChainCenterServer.getColdChainCenterByAreaId("451022");
        System.out.println(coldChainCenterByAreaId);

    }
}
