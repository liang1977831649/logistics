package com.logistics.mapper;

import com.logistics.entity.Money;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MoneyMapper {

    @Insert("insert into money(id, user_id, car_num, balance) value (null,#{userId},#{carNum},#{balance})")
    void insertAccountForUser(Money money);

    @Select("select * from money where user_id=#{userId}")
    Money selectMoneyByUserId(String userId);

    void updateMoneyByUserId(Money money);
}
