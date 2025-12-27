package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LiquidationPlanRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    private Long doctorId;

    private String medicalShopName;

    @NotNull
    @Min(1)
    private Integer targetLiquidation;

    @NotNull
    private LocalDateTime deadline;

    private String strategy;
}
