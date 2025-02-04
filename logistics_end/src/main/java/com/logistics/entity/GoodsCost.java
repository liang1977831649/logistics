package com.logistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class GoodsCost {
    private String id;
    private String buyerId;
    private String salesId;
    private String deliId;
    private Float cost;
    private Integer status;
    private String goodsId;
    private String userName;
    private String goodsName;
    private String weight;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updateTime;

}
