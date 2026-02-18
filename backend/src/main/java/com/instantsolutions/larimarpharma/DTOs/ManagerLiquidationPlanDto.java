package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManagerLiquidationPlanDto {

    private Long id;
    private String product;
    private Integer quantity;   // availableUnits
    private String doctor;
    private Integer targetLiquidation;
    private Integer achievedUnits;
    private String marketName;
    private String medicalShopName;
    private String status;   // managerApprovalStatus
    private LocalDateTime createdAt;
    private Long employeeId;
}

