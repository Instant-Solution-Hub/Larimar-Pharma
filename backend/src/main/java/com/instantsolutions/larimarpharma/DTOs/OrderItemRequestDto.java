package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

@Data
public class OrderItemRequestDto {
    private Long productId;
    private Integer quantity;
    private Double price;
}

