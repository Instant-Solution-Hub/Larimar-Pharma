package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;
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

    // Product
    private Long productId;
    private String productName;
    private String productCategory;

    // Doctor
    private Long doctorId;
    private String doctorName;

    private String hospitalName;
    private String observations;
    private String imageUrl;

    private boolean managerNotified;

    private LocalDateTime reportedDate;
    private LocalDateTime createdAt;
}
