package com.logistics.mapper;

import com.logistics.entity.ColdChainCenter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ColdChainCenterMapper {
    @Select("select * from cold_chain_center where area_id=#{areaId}")
    List<ColdChainCenter> getColdChainCenterByAreaId(String areaId);

}
