package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveRequestWithFEResponseDto {

    private Long id;

    private String feCode;
    private String feName;

    private LeaveRequest.LeaveType leaveType;
    private LeaveRequest.ApprovalStatus status;

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String reason;
    private LocalDateTime appliedDate;
}

