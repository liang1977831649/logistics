package com.logistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
public class Driver {
    @NotNull
    @Pattern(regexp = "sj.+")
    private String id;
    @NotNull
    private String name;
    @NotNull
    private String phone;
    private Integer sex;
    private Integer status;
    private String area;
    private String areaId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;
}
