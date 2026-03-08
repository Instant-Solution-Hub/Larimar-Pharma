package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedSlotPlanRequestCheckDto {
    private boolean hasApprovedRequest;
    private LocalDate currentDate;
    private Long requestId;
    private String reason;
    private String requesterType; // "MANAGER" or "FE"
    private String requesterName;
    private String message;
}