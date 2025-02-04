package com.logistics.mapper;

import com.logistics.entity.Driver;
import com.logistics.entity.TsCar;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TsCarMapper {
    List<TsCar> selectTsCarList(String id, String carId, String driverName, String driverId, String userId,Integer status,String areaId);


    TsCar selectTsCarById(String id);

    void insertTsCar(TsCar tsCar);

    void update(TsCar tsCar);

    @Delete("delete from ts_car where id=#{id}")
    void delete(String id);

    @Select("select * from ts_car where driver_id=#{driverId}")
    TsCar selectTsCarByDriverId(String driverId);

    @Select("select * from ts_car where car_id=#{carId}")
    TsCar selectTsCarByCarId(String carId);
}
