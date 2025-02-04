package com.logistics.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Money {
    private Integer id;
    private String userId;
    private String carNum;
    private Float balance;
}
