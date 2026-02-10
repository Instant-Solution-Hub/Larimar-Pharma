package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SlotChangeRequestDto {
    private Long visitId;
    private Long userId;
    private Long managerVisitId;
//    private LocalDate requestedVisitDate;
    private Integer requestedWeekNumber;
    private Integer requestedDayOfWeek;
    private String reason;
    private String userType;
}
