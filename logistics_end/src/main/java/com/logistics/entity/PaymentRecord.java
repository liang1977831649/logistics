package com.logistics.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PaymentRecord {
    private Integer id;
    private String payId;
    private String collectId;
    private Float cost;
    @NotNull
    private Integer payType;
    @NotNull
    private Integer transaction;
    @NotNull
    private String transactionId;
    private String userName;
    private String userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}