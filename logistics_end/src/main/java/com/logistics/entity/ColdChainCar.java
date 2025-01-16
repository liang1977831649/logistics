package com.logistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ColdChainCar {
    @NotNull
    private String id;
    @NotNull
    private String name;
    private Float tem;
    private Float hum;
    private Float curWeight;
    @NotNull
    private Float weight;//按千克算
    private Float curVolume;
    @NotNull
    private Float volume;//按立方米算\
    @NotNull(groups = Update.class)
    private Integer status;
    private String area;
    private String areaId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    public interface Update{}
}
