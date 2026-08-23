package com.instantsolutions.larimarpharma.DTOs;
import com.instantsolutions.larimarpharma.entity.SlotPlanningDayRequest;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SlotPlanningDayRequestResponseDto {
    private Long id;
    private String reason;
    private Long requestedManagerId;
    private String requestedManagerName;
    private Long requestedZsmId;
    private String requestedZsmName;
    private Long requestedFieldExecutiveId;
    private String requestedFieldExecutiveName;
    private SlotPlanningDayRequest.RequestStatus status;
    private LocalDate requestedAt;
    private LocalDate reviewedAt;
    private String adminNotes;
}