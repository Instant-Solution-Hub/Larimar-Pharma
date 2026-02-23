package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.DoctorConversionRequestDto;
import com.instantsolutions.larimarpharma.DTOs.DoctorConversionResponseDto;
import com.instantsolutions.larimarpharma.service.DoctorConversionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctor-conversions")
@RequiredArgsConstructor
public class DoctorConversionController {

    private final DoctorConversionService doctorConversionService;

    @PostMapping
    public DoctorConversionResponseDto addDoctorConversion(
            @Valid @RequestBody DoctorConversionRequestDto request) {

        return doctorConversionService.addDoctorConversion(request);
    }

    @GetMapping("/current-month")
    public ResponseEntity<ApiResponseDto<List<DoctorConversionResponseDto>>>
    getCurrentMonthDoctorConversions() {

        List<DoctorConversionResponseDto> conversions =
                doctorConversionService.getCurrentMonthConversions();

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        conversions,
                        "Doctor conversions for current month fetched successfully"
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<DoctorConversionResponseDto>>>
    getDoctorConversionsBetweenDates(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {

        List<DoctorConversionResponseDto> conversions =
                doctorConversionService.getConversionsBetweenDates(fromDate, toDate);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        conversions,
                        "Doctor conversions fetched successfully"
                )
        );
    }


    @DeleteMapping("/{feId}/{conversionId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteDoctorConversion(
            @PathVariable Long feId,
            @PathVariable Long conversionId
    ) {

        doctorConversionService.deleteDoctorConversion(feId, conversionId);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        null,
                        "Doctor conversion deleted successfully"
                )
        );
    }



}

