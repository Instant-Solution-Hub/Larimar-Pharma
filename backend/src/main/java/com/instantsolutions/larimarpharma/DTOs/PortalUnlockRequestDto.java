package com.instantsolutions.larimarpharma.DTOs;


import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.PortalUnlockRequest;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PortalUnlockRequestDto {

    private Long id;

    private PortalUnlockRequest.UserType userType;
    private LocalDate lockedDate;
    private String reason;
    private PortalUnlockRequest.ApprovalStatus status;

    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;

    private String adminComments;

    private ManagerResponseDto manager;
    private FieldExecutiveResponse fieldExecutive;


}
