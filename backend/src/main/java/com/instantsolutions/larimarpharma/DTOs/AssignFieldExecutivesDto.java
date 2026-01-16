package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class AssignFieldExecutivesDto {
    @NotEmpty
    private Set<Long> fieldExecutiveIds;
}
