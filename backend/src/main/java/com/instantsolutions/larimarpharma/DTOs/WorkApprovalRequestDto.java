package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkApprovalRequestDto {



        private LocalDate workDate;
        private Long fieldExecutiveId;
        private Long managerId;
        private String description;


}
