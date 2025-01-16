package com.logistics.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Rm_Gs {
    private Integer id;
    private String rmId;
    private String gdId;
    private LocalDateTime createTime;
}
