package com.instantsolutions.larimarpharma.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OfferResponseDto {
    private Long id;
    private String offerCategory;
    private String offerName;
    private String imageUrl;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
