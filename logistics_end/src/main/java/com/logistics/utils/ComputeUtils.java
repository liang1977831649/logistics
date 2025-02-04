package com.logistics.utils;

import com.logistics.entity.RoomCostCompute;
import com.logistics.mapper.RoomCostComputeMapper;
import io.netty.util.internal.StringUtil;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class ComputeUtils {

    public static Float  computeVolumeByWeight(Float volume,Float MaxWeight,Float weight){
        Float percentage=weight/MaxWeight;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return java.lang.Float.valueOf( decimalFormat.format(volume*percentage));
    }
    public static Float computeCostForGoods(Float weight,Float price){
        return weight*price;
    }
    public static Integer computeLocalDateTimeSub( LocalDateTime star,LocalDateTime end){
        long between = ChronoUnit.HOURS.between(star ,end);
        Integer days =(int) Math.ceil( Float.valueOf( between) / Float.valueOf( 24));
        if(days==0){
            days=1;
        }
        return days;
    }
    public static Float computeCostForRoom(Float volume, Float weight,Integer days){
        //重量Kg转换成吨
        RoomCostComputeMapper roomCostComputeMapper = getRoomCostComputeMapperObject();
        String areaId=(String)((HashMap<String,Object>)ThreadLocalUtils.get()).get("areaId");
        RoomCostCompute roomCostCompute = roomCostComputeMapper.getRoomCostComputeByAreaId(areaId);
        Float volumePrice=roomCostCompute.getVolumePrice();
        Float weightPrice=roomCostCompute.getWeightPrice();
        Float servicePrice=roomCostCompute.getOther();
        return (volume*volumePrice+(weight/1000)*weightPrice)*days+servicePrice;

    }

    public static RoomCostComputeMapper getRoomCostComputeMapperObject(){
        RoomCostComputeMapper bean = SpringUtil.getBean(RoomCostComputeMapper.class);
        return bean;
    }

}
