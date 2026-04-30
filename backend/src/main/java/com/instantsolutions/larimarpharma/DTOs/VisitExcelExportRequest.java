package com.instantsolutions.larimarpharma.DTOs;


import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.entity.Visit.VisitStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class VisitExcelExportRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long fieldExecutiveId;  // optional - can be null
    private Long managerId;  // optional - can be null

    private VisitStatus visitStatus; // optional - can be null
    private Doctor.Category category;// optional - can be null
}