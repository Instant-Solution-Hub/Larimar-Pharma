package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitiveBrandReportRequestDto {

    @NotNull(message = "Field Executive ID is required")
    private Long fieldExecutiveId;

    @NotBlank(message = "Brand name is required")
    @Size(max = 100)
    private String brandName;

    @NotBlank(message = "Company name is required")
    @Size(max = 100)
    private String companyName;


    private String productName;

    @NotBlank(message = "Product category is required")
    private String productCategory;

    @NotNull(message = "Resource person name is required")
    private String source;

    @NotBlank(message = "Resource person designation is required")
    private String designation;

    private String observations;

    private boolean managerNotified;

}

