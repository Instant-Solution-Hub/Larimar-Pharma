package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.DoctorChangeRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DoctorChangeReviewRequestDto {

    @NotNull
    private DoctorChangeRequest.RequestStatus status;

}