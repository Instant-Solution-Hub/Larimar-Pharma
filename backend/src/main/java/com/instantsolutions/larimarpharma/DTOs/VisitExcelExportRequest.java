package com.instantsolutions.larimarpharma.DTOs;


import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.entity.Visit.VisitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitExcelExportRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long fieldExecutiveId;  // optional - can be null
    private Long managerId;  // optional - can be null

    private VisitStatus visitStatus; // optional - can be null
    private Doctor.Category category;// optional - can be null
    private Doctor.PracticeType docType;// optional - can be null
}