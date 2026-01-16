package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Visit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceRecordDto {
    private String id;
    private String name;
    private String category;
    private LocalDate scheduledDate;
    private String status; // "completed", "missed"
    private Integer week;
    private String visitType; // "doctor", "pharmacist"
    private String reason; // Optional - for missed visits
}
