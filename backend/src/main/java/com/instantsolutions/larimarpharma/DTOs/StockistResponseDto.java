package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Stockist.StockistType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockistResponseDto {

    private Long id;
    private String name;
    private StockistType type;
    private String contactPerson;
    private String contactNumber;
    private String location;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
