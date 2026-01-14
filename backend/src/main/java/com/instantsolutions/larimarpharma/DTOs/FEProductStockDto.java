package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FEProductStockDto {

    private Long productId;
    private String productName;
    private Integer allocatedQuantity;
    private Integer remainingQuantity;
}
