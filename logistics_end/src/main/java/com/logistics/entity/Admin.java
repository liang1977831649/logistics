package com.logistics.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Admin extends Account{
    @NotNull(groups = Add.class)
    private String password;
    private String phone;

    public interface Add{}
}
