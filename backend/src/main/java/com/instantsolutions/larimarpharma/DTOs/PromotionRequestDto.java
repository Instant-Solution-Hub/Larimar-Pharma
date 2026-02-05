package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Promotion;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRequestDto {

    @NotBlank(message = "Promotion name is required")
    @Size(min = 3, max = 100, message = "Promotion name must be between 3 and 100 characters")
    private String name;


    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Promotion type is required")
    private Promotion.Type type;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @NotNull(message = "Promotion status is required")
    private Promotion.PromotionStatus status;

    @NotBlank(message = "Product is required")
    private String product;

    private List<String> benefits;

    @NotEmpty(message = "Target audience cannot be empty")
    private List<
            @NotBlank(message = "Target audience value cannot be blank")
                    String
            > targetAudience;
}

