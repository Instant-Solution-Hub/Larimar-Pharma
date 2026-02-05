package com.instantsolutions.larimarpharma.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManagerStockistResponseDto {
    private Long id;
    private String name;
    private String marketName;
}

