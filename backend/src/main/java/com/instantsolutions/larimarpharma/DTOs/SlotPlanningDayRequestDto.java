package com.instantsolutions.larimarpharma.DTOs;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class SlotPlanningDayRequestDto {

    @NotBlank(message = "Reason is required")
    private String reason;

    private Long requestedManagerId;  // Required if requesting as Manager
    private Long requestedFieldExecutiveId;  // Required if requesting as FE
    private LocalDate requestedAt;

    private String adminNotes;
}

