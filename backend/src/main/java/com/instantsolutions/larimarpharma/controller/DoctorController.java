package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.DoctorRequestDto;
import com.instantsolutions.larimarpharma.DTOs.DoctorResponseDto;
import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // CREATE
    @PostMapping
    public ApiResponseDto<DoctorResponseDto> createDoctor(@Valid @RequestBody DoctorRequestDto dto) {
        DoctorResponseDto doctor = doctorService.create(dto);
        return ApiResponseDto.success(doctor, "Doctor created successfully");
    }

    // UPDATE
    @PutMapping("/{id}")
    public ApiResponseDto<DoctorResponseDto> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequestDto dto
    ) {
        DoctorResponseDto doctor = doctorService.update(id, dto);
        return ApiResponseDto.success(doctor, "Doctor updated successfully");
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ApiResponseDto<DoctorResponseDto> getDoctor(@PathVariable Long id) {
        DoctorResponseDto doctor = doctorService.getById(id);
        return ApiResponseDto.success(doctor, "Doctor fetched successfully");
    }

    // GET ALL
    @GetMapping
    public ApiResponseDto<List<DoctorResponseDto>> getAllDoctors() {
        return ApiResponseDto.success(
                doctorService.getAllActive(),
                "Active doctors fetched successfully"
        );
    }

    // GET ALL FOR ADMIN (INACTIVE AND ACTIVE)
    @GetMapping("/all")
    public ApiResponseDto<List<DoctorResponseDto>> getAllDoctorsAdmin() {
        return ApiResponseDto.success(
                doctorService.getAll(),
                "All doctors fetched successfully"
        );
    }


    // DELETE
    @DeleteMapping("/{id}")
    public ApiResponseDto<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.delete(id);
        return ApiResponseDto.success(null, "Doctor deactivated successfully");
    }

    // RE-ACTIVATE
    @PutMapping("/{id}/activate")
    public ApiResponseDto<DoctorResponseDto> activateDoctor(@PathVariable Long id) {
        doctorService.activate(id);
//        doctor.setActive(true);
        return ApiResponseDto.success(null,"Doctor activated successfully");
    }

    @PutMapping("/{doctorId}/assign-fe/{feId}")
    public ResponseEntity<DoctorResponseDto> assignFE(
            @PathVariable Long doctorId,
            @PathVariable Long feId
    ) {
        return ResponseEntity.ok(
                doctorService.assignDoctorToFE(doctorId, feId)
        );
    }


}
