package com.instantsolutions.larimarpharma.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.CompetitiveBrandReportRequestDto;
import com.instantsolutions.larimarpharma.DTOs.CompetitiveBrandReportResponseDto;
import com.instantsolutions.larimarpharma.service.CompetitiveBrandReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/competitive-reports")
public class CompetitiveBrandReportController {

    @Autowired
    private CompetitiveBrandReportService reportService;

    // ✅ CREATE
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<CompetitiveBrandReportResponseDto>> create(
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {

        CompetitiveBrandReportRequestDto dto =
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .readValue(data, CompetitiveBrandReportRequestDto.class);

        CompetitiveBrandReportResponseDto saved =
                reportService.create(dto, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(saved, "Report created successfully"));
    }

    // ✅ UPDATE
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<CompetitiveBrandReportResponseDto>> update(
            @PathVariable Long id,
            @RequestPart("data") CompetitiveBrandReportRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        CompetitiveBrandReportResponseDto updated =
                reportService.update(id, dto, image);

        return ResponseEntity.ok(
                ApiResponseDto.success(updated, "Report updated successfully")
        );
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CompetitiveBrandReportResponseDto>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        reportService.getById(id),
                        "Report fetched successfully"
                )
        );
    }

    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CompetitiveBrandReportResponseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        reportService.getAll(),
                        "Reports fetched successfully"
                )
        );
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {
        reportService.delete(id);

        return ResponseEntity.ok(
                ApiResponseDto.success(null, "Report deleted successfully")
        );
    }
}
