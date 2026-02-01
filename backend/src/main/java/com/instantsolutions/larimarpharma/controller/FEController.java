package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.service.AttendanceService;
import com.instantsolutions.larimarpharma.service.FEVisitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import com.instantsolutions.larimarpharma.service.FEService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/field-executives")
@RequiredArgsConstructor
public class FEController {

    @Autowired
    AttendanceService attendanceService;
    @Autowired
    FEService fieldExecutiveService;
    @Autowired
    FEVisitService feVisitService;
    final FEService service;

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


    @PostMapping
    public ResponseEntity<ApiResponseDto<FieldExecutiveResponse>> create(@Valid @RequestBody FieldExecutiveRequest request) {
        FieldExecutiveResponse response = service.create(request);
        return ResponseEntity.ok(
                ApiResponseDto.success(response, "User Created Successfully!")
        );

    }

    @GetMapping
    public List<FieldExecutiveResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public FieldExecutiveResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<FieldExecutiveResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody FieldExecutiveRequest request
    ) {
        FieldExecutiveResponse response = service.update(id, request);
        return ResponseEntity.ok(
                ApiResponseDto.success(response, "User Updated Successfully!")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.ok(
                ApiResponseDto.success(null, "User deleted successfully")
        );
    }

    @GetMapping("/{feId}/doctors")
    public ResponseEntity<ApiResponseDto<List<DoctorResponseDto>>> getAllocatedDoctors(
            @PathVariable Long feId
    ) {
        List<DoctorResponseDto> doctors = fieldExecutiveService.getAllocatedDoctors(feId);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        doctors,
                        "Doctors fetched successfully"
                )
        );
    }

    @PutMapping("/{feId}/contact/basic")
    public ResponseEntity<ApiResponseDto<FEContactResponseDto>> updateContactDetails(
            @PathVariable Long feId,
            @RequestBody FEContactUpdateRequestDto dto
    ) {
        FEContactResponseDto updated =
                fieldExecutiveService.updateContactDetails(feId, dto);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        updated,
                        "Contact details updated successfully"
                )
        );
    }

    @GetMapping("/{feId}/profile-stats")
    public ResponseEntity<FEProfileStatsResponseDto> getProfileStats(
            @PathVariable Long feId
    ) {
        return ResponseEntity.ok(
                fieldExecutiveService.getProfileStats(feId)
        );
    }

    @GetMapping("/{feId}/contact")
    public ResponseEntity<ApiResponseDto<FEContactResponseDto>> getContactDetails(
            @PathVariable Long feId
    ) {
        FEContactResponseDto response =
                fieldExecutiveService.getContactDetails(feId);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        response,
                        "Contact details fetched successfully"
                )
        );


    }

    @GetMapping("/manager/{managerId}/a-priority-field-executives")
    public ResponseEntity<List<FieldExecutiveResponse>> getAPriorityFEs(
            @PathVariable Long managerId,
            @RequestParam Integer weekNumber,
            @RequestParam Integer dayOfWeek
    ) {
        return ResponseEntity.ok(
                fieldExecutiveService.getFEsWithAPriorityVisits(
                        managerId,
                        weekNumber,
                        dayOfWeek
                )
        );
    }



}
