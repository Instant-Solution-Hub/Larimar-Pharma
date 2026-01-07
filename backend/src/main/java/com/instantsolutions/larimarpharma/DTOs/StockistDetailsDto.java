package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Stockist;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockistDetailsDto {

    private Long id;
    private String name;
    private Stockist.StockistType type;
    private String contactPerson;
    private String contactNumber;
    private String location;
    private boolean active;
}
