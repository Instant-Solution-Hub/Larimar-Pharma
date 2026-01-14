package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FEProductAllocationRequestDto {

    @NotNull
    private Long feId;

    @NotNull
    private Long productId;

    @Min(1)
    private Integer quantity;
}
