package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompanyTargetRequestDto {

    private Integer year;
    private Integer month;
    private BigDecimal targetAmount;
    private BigDecimal currentProgress;

}

