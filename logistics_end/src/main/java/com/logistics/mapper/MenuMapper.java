package com.logistics.mapper;

import com.logistics.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {
    List<Menu> getMenuByRole(String role);
}
