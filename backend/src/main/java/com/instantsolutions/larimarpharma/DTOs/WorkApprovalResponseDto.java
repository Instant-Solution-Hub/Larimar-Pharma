package com.instantsolutions.larimarpharma.DTOs;


import com.instantsolutions.larimarpharma.entity.ApprovalRequest;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class WorkApprovalResponseDto {



        private Long id;
        private LocalDate workDate;
        private String requestedByName;
        private String requestedByRole;
        private ApprovalRequest.ApprovalStatus status;
        private String remarks;


}
