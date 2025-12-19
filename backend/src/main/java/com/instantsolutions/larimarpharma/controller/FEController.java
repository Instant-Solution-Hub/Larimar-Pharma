package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.FEUpdateContactDto;
import com.instantsolutions.larimarpharma.DTOs.MonthlyDoctorVisitDto;
import com.instantsolutions.larimarpharma.DTOs.MonthlyDoctorVisitStatDto;
import com.instantsolutions.larimarpharma.service.AttendanceService;
import com.instantsolutions.larimarpharma.service.FEVisitService;
import com.instantsolutions.larimarpharma.service.FieldExecutiveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fe")
public class FEController {
    @Autowired
    AttendanceService attendanceService;
    @Autowired
    FieldExecutiveService fieldExecutiveService;
    @Autowired
    FEVisitService feVisitService;

    @GetMapping("/{feId}/doctor-visits/monthly")
    public MonthlyDoctorVisitStatDto getMonthlyDoctorVisits(
            @PathVariable Long feId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return feVisitService.getMonthlyDoctorVisitStats(feId, year, month);
    }

    @GetMapping("/{feId}/doctor-visits/current-month")
    public List<MonthlyDoctorVisitDto> getCurrentMonthDoctorVisits(
            @PathVariable Long feId
    ) {
        return feVisitService.getCurrentMonthDoctorVisits(feId);
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponseDto<Integer>> getMonthlyAttendance(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam Long feId) {

        Integer attendance =
                attendanceService.calculateMonthlyAttendance(feId, year, month);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        attendance,
                        "Attendance calculated successfully"
                )
        );
    }

    @PutMapping("/{feId}/contact")
    public ResponseEntity<ApiResponseDto<String>> updateContactDetails(
            @PathVariable Long feId,
            @Valid @RequestBody FEUpdateContactDto dto
    ) {
        fieldExecutiveService.updateContactDetails(feId, dto);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        null,
                        "Contact details updated successfully"
                )
        );
    }
}
