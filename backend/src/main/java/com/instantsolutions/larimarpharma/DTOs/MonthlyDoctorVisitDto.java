package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyDoctorVisitDto {

    private Long visitId;

    private String doctorName;
    private String doctorCategory;

    private LocalDateTime scheduledDate;
    private String status;

    private String reason; // populated only if MISSED
}
