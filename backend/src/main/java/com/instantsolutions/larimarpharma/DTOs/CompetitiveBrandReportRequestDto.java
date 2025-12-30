package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

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


    private Long productId;

    @NotBlank(message = "Product category is required")
    private String productCategory;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    private String observations;

    private boolean managerNotified;

    @NotNull(message = "Reported date is required")
    @PastOrPresent(message = "Reported date cannot be in the future")
    private LocalDateTime reportedDate;
}

