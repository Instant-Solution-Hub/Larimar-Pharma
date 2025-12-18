package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.DoctorRequestDto;
import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // CREATE
    @PostMapping
    public ApiResponseDto<Doctor> createDoctor(@RequestBody DoctorRequestDto dto) {
        Doctor doctor = doctorService.create(dto);
        return ApiResponseDto.success(doctor, "Doctor created successfully");
    }

    // UPDATE
    @PutMapping("/{id}")
    public ApiResponseDto<Doctor> updateDoctor(
            @PathVariable Long id,
            @RequestBody DoctorRequestDto dto
    ) {
        Doctor doctor = doctorService.update(id, dto);
        return ApiResponseDto.success(doctor, "Doctor updated successfully");
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ApiResponseDto<Doctor> getDoctor(@PathVariable Long id) {
        Doctor doctor = doctorService.getById(id);
        return ApiResponseDto.success(doctor, "Doctor fetched successfully");
    }

    // GET ALL
    @GetMapping
    public ApiResponseDto<List<Doctor>> getAllDoctors() {
        return ApiResponseDto.success(
                doctorService.getAllActive(),
                "Active doctors fetched successfully"
        );
    }

    // GET ALL FOR ADMIN (INACTIVE AND ACTIVE)
    @GetMapping("/all")
    public ApiResponseDto<List<Doctor>> getAllDoctorsAdmin() {
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
    public ApiResponseDto<Doctor> activateDoctor(@PathVariable Long id) {
        Doctor doctor = doctorService.getById(id);
        doctor.setActive(true);
        return ApiResponseDto.success(doctor, "Doctor activated successfully");
    }


}
