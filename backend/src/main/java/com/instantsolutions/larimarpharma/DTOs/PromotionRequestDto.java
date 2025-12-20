package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Promotion;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRequestDto {

    private String name;
    private String description;
    private Promotion.Type type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
}
