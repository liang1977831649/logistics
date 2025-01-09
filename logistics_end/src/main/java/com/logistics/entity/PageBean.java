package com.logistics.entity;

import lombok.Data;

import java.util.List;

@Data
public class PageBean<T> {
    private Integer total;
    private List<T> items;
}
