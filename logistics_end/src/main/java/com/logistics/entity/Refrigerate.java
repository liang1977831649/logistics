package com.logistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Refrigerate {
    @NotNull
    private String id;
    @NotNull
    private String name;
    private Float tem;
    private Float hum;
    private Float curVolume;
    @NotNull
    private Float maxVolume;
    private Integer status;
    @NotNull
    private String cccId;
    private String centreName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;
}
