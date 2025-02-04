package com.logistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TsCar {
    private String id;
    @NotNull
    private String carId;
    private String carName;
    @NotNull
    private String driverId;
    private String driverName;
    private String driverPhone;
    private String areaId;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updateTime;

}
