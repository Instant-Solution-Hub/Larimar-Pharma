package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FEProductAllocationResponseDto {

    private Long feId;
    private Long productId;
    private String productName;

    private Integer allocatedQuantity;
    private Integer remainingQuantity;
}

