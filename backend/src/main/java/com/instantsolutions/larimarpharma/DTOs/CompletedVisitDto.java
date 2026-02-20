package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.Visit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompletedVisitDto {

    /* ===== Visit Core ===== */
    private Long visitId;
    private Visit.VisitType visitType;   // DOCTOR / PHARMACIST / STOCKIST

    private Visit.VisitStatus status;

    private LocalDate visitDate;
    private Integer weekNumber;
    private Integer dayOfWeek;

    private LocalDateTime actualVisitTime;
    private String location;
    private String notes;
    private String feName;
    private String feEmpCode;
    private Long feId;

    private String managerName;
    private String managerEmpCode;

    private String userRole;

    /* ===== Execution Details ===== */
    private List<String> activitiesPerformed;
    private Double orderValue;

    /* ===== Entity-specific (only one non-null) ===== */
    private DoctorDetailsDto doctor;
    private PharmacyDetailsDto pharmacy;
    private StockistDetailsDto stockist;
}
