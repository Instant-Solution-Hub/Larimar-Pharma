package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.LiquidationPlan;
import lombok.Data;

@Data
public class UpdateLiquidationApprovalDto {
    private LiquidationPlan.ApprovalStatus status; // APPROVED or REJECTED
}

