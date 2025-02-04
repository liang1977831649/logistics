package com.logistics.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Goods {
    @NotNull(groups = Update.class)
    private String id;
    @NotNull
    private String name;
    @NotNull
    private Float weight;
    @NotNull
    private Float volume;
    @NotNull(groups = Update.class)
    private Float tem;
    @NotNull(groups = Update.class)
    private Float hum ;
    @NotNull
    private Float price;
    @NotNull(groups = Update.class)
    private String userId;
    private String goodsPic;
    private String rmId;
    private String rmName;
    private Integer status;
    private String userName;
    public interface Update{};

}
