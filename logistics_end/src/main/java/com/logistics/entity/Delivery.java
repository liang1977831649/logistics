package com.logistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Delivery {
    @NotNull(groups = Loading.class)
    private String id;
    private String userId;
    private String userName;
    @NotNull(groups = Add.class)
    private String goodsId;
    private String goodsName;
    @NotNull(groups = Add.class)
    private Float weight;
    private Float volume;
    @NotNull(groups = Add.class)
    private String destination;
    private Integer status;
    @NotNull(groups = Loading.class)
    private String carDriId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime departTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updateTime;

    public interface Add{}
    public interface Loading{}
}
