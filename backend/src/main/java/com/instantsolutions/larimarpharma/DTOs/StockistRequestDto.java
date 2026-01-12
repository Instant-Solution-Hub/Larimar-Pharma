package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Stockist.StockistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockistRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private StockistType type;

    private String contactPerson;
    private String contactNumber;
    private String location;
}
