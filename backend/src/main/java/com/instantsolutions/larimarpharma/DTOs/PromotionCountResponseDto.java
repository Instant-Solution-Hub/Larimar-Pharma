package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PromotionCountResponseDto {

    private long totalPromotions;
    private long activePromotions;
    private long upcomingPromotions;
}
