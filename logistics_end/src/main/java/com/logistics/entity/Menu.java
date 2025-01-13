package com.logistics.entity;

import lombok.Data;

import java.util.List;

@Data
public class Menu {
    private Integer id;
    private Integer menuType;
    private String MenuName;
    private String path;
    private String component;
    private String icon;
    private Integer father;
    private String role;
    private List<Menu> menuList;
}
