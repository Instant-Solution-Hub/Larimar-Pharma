package com.instantsolutions.larimarpharma.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LiquidationPlanResponseDto {

    private Long id;
    private Long productId;
    private String productName;

    private Long doctorId;
    private String doctorName;
    private String marketName;

    private Integer targetLiquidation;
    private Integer achievedUnits;

    private String medicalShopName;
    private LocalDateTime deadline;
    private String strategy;

    private LocalDateTime createdAt;
}
