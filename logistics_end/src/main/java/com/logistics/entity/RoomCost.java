package com.logistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomCost {
    @NotNull
    private String id;
    private String userId;
    private String deliId;
    private Integer day;
    @NotNull
    private Float cost;
    private Float volume;
    private Float weight;
    private String goodsName;
    private String goodsId;
    private Integer status;
    private String userName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updateTime;
}
