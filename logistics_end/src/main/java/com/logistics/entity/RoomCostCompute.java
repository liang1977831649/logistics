package com.logistics.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomCostCompute {
    @NotNull
    private Float weightPrice;
    @NotNull
    private Float volumePrice;
    @NotNull
    private Float other;
    private String areaId;

}
