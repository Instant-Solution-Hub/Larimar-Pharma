package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitiveBrandReportResponseDto {

    private Long id;

    // Field Executive
    private Long fieldExecutiveId;
    private String fieldExecutiveName;

    // Brand info
    private String brandName;
    private String companyName;

    // Product

    private String productName;
    private String productCategory;

    // Doctor

    private String source;

    private String designation;
    private String observations;
    private String imageUrl;

    private boolean managerNotified;


    private LocalDateTime createdAt;
}
