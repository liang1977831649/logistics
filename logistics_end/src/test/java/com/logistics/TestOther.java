package com.logistics;

import com.logistics.entity.Delivery;
import com.logistics.entity.Goods;
import com.logistics.entity.Rm_Gs;
import com.logistics.entity.RoomCostCompute;
import com.logistics.mapper.DeliveryMapper;
import com.logistics.mapper.GoodsMapper;
import com.logistics.mapper.Rm_GsMapper;
import com.logistics.mapper.RoomCostComputeMapper;
import com.logistics.utils.ComputeUtils;
import com.logistics.utils.DateCreateUtil;
import com.logistics.utils.ThreadLocalUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@SpringBootTest
public class TestOther {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private DeliveryMapper deliveryMapper;
    @Autowired
    private Rm_GsMapper rmGsMapper;

    @Test
    public void date() {
        String s = DateCreateUtil.dateFileName();
        System.out.println(s);
    }

    @Test
    public void createNo() {
        String id = "gs" + UUID.randomUUID().toString().replace('-', 'x').substring(0, 10);
        System.out.println(id);
    }

    @Test
    public void dateCompute() {
        Delivery delivery = deliveryMapper.selectDeliveryById("de3917695cx2");
        Rm_Gs rmGsByGoodsId = rmGsMapper.selectRm_GsByGoodsId(delivery.getGoodsId());
        long between = ChronoUnit.HOURS.between(rmGsByGoodsId.getCreateTime(), delivery.getDepartTime());
        Integer days = (int) Math.ceil(Float.valueOf(between) / Float.valueOf(24));
        System.out.println(days);
    }

    @Test
    public void getMapper() {
        RoomCostComputeMapper roomCostComputeMapperObject = ComputeUtils.getRoomCostComputeMapperObject();
        RoomCostCompute roomCostComputeByAreaId = roomCostComputeMapperObject.getRoomCostComputeByAreaId("451022");
        System.out.println(roomCostComputeByAreaId);
    }

    @Test
    public void testTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse("2025-02-03 12:49:06", dateTimeFormatter);
        LocalDateTime now = LocalDateTime.now();
        Integer integer = ComputeUtils.computeLocalDateTimeSub(parse, now);
        System.out.println(integer);
    }
}
