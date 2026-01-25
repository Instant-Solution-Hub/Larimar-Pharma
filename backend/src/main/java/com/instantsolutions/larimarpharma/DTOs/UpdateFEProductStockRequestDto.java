package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFEProductStockRequestDto {

    @NotNull
    private Long feId;

    @NotNull
    private Long productId;

    @Min(1)
    private Integer newAllocatedQuantity;
}
