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
    private float tem;
    private float hum;
    @NotNull
    private float weight;//按千克算
    @NotNull
    private float volume;//按立方米算\
    @NotNull(groups = Update.class)
    private Integer status;
    private String area;
    private String areaId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    public interface Update{}
}
