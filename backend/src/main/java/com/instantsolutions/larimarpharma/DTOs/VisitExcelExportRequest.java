package com.instantsolutions.larimarpharma.DTOs;


import com.instantsolutions.larimarpharma.entity.Visit.VisitStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class VisitExcelExportRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long fieldExecutiveId;  // optional - can be null
    private VisitStatus visitStatus; // optional - can be null
}