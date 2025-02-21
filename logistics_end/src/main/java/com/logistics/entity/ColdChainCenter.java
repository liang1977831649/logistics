package com.logistics.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ColdChainCenter {
    @NotNull
    private String id;
    @NotNull
    private String name;

    private String areaId;
    @NotNull
    private String location;
}
