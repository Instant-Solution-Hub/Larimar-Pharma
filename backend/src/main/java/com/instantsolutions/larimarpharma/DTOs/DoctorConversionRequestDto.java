package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DoctorConversionRequestDto {

    @NotNull(message="doctor id is required")
    private Long doctorId;

    @NotNull(message="product id is required")
    private Long productId;


    @NotNull(message="field executive id is required")
    private Long fieldExecutiveId;


}



