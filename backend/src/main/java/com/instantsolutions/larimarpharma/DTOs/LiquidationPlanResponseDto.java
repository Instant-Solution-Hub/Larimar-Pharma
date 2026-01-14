package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import com.instantsolutions.larimarpharma.entity.LiquidationPlan;
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
    private Integer quantity;

    private LocalDateTime createdAt;
    private LiquidationPlan.ApprovalStatus managerApprovalStatus;
}
