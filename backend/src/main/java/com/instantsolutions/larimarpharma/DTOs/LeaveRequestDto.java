package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.LeaveRequest.LeaveType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveRequestDto {

    private Long fieldExecutiveId;
    private LeaveType leaveType;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String reason;
}
