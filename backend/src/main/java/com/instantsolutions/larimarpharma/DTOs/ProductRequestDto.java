package com.instantsolutions.larimarpharma.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    private String name;
    private String category;
    private String description;
    private Double price;
    private boolean active;
}
