package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MonthlyTargetPlanRequestDto {

    @NotNull(message = "Field Executive ID is required")
    private Long fieldExecutiveId;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be valid")
    private Integer year;

    @NotNull(message = "Primary target is required")
    @PositiveOrZero(message = "Primary target cannot be negative")
    private Double primaryTarget;

    @NotNull(message = "Secondary target is required")
    @PositiveOrZero(message = "Secondary target cannot be negative")
    private Double secondaryTarget;
}
