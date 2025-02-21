package com.logistics.mapper;

import com.logistics.entity.ColdChainCenter;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ColdChainCenterMapper {
    @Select("select * from cold_chain_center where area_id=#{areaId}")
    List<ColdChainCenter> getColdChainCenterByAreaId(String areaId);

    List<ColdChainCenter> selectColdChainCenterByAreaId(String id, String name,String areaId);

    @Select("select id,name,area_id,location from cold_chain_center where id=#{id}")
    ColdChainCenter selectColdChainCenterById(String id);

    void addColdChainServer(ColdChainCenter coldChainCenter);

    void updateColdChainCenter(ColdChainCenter coldChainCenter);

    @Delete("delete from cold_chain_center where id=#{id}")
    void deleteColdChainCenter(String id);
}
