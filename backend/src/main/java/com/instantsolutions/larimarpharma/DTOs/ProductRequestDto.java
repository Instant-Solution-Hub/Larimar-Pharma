package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Product category is required")
    private String category;

    @NotBlank(message = "Product description is required")
    @Size(max = 40 , message = "Limit is 40 letters")
    private String description;

    @NotNull(message = "Product price is required")
    private Double price;

    @NotNull(message = "Product pts is required")
    private Double pts;

    @NotNull(message = "Product ptr is required")
    private Double ptr;

    private boolean active;
}
