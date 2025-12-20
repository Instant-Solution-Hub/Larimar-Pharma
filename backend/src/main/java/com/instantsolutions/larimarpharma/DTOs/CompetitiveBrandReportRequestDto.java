package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitiveBrandReportRequestDto {

    private Long fieldExecutiveId;
    private String brandName;
    private Long productId;
    private String productCategory;
    private Long doctorId;
    private String hospitalName;
    private String observations;
    private boolean managerNotified;
    private LocalDateTime reportedDate;
}
