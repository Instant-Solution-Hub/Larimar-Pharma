package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.SlotPlanningDayRequest;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class SlotPlanningDayRequestUpdateDto {
    @NotNull(message = "Status is required")
    private SlotPlanningDayRequest.RequestStatus status;

    private String adminNotes;
}