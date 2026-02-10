package com.instantsolutions.larimarpharma.DTOs;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data

public class SlotChangeRequestResponse {
    private Long id;
    private Long visitId;
    private Long managerVisitId;
    private String visitType;
    private String targetName;
    private String currentSchedule;
    private String requestedSchedule;
    private String requestedByName;
    private String requestedByRole;
    private String approvedByName;
    private String status;
    private String reason;
    private String adminNotes;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
}