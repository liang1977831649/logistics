package com.logistics.mapper;

import com.logistics.entity.ColdChainCenter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ColdChainCenterMapper {
    @Select("select * from cold_chain_center where area_id=#{areaId}")
    ColdChainCenter getColdChainCenterByAreaId(String areaId);
}
