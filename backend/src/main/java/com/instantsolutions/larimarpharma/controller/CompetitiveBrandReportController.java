package com.instantsolutions.larimarpharma.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.CompetitiveBrandReportRequestDto;
import com.instantsolutions.larimarpharma.entity.CompetitiveBrandReport;
import com.instantsolutions.larimarpharma.service.CompetitiveBrandReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/competitive-reports")
@RequiredArgsConstructor
public class CompetitiveBrandReportController {

    @Autowired
    CompetitiveBrandReportService reportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<CompetitiveBrandReport>> create(
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        CompetitiveBrandReportRequestDto dto =
                mapper.readValue(data, CompetitiveBrandReportRequestDto.class);

        CompetitiveBrandReport saved = reportService.create(dto, image);

        return new ResponseEntity<>(
                ApiResponseDto.success(saved, "Report created successfully"),
                HttpStatus.CREATED
        );
    }


    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponseDto<CompetitiveBrandReport>> update(
            @PathVariable Long id,
            @RequestPart("data") CompetitiveBrandReportRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        CompetitiveBrandReport updated = reportService.update(id, dto, image);
        return ResponseEntity.ok(
                ApiResponseDto.success(updated, "Report updated successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CompetitiveBrandReport>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        reportService.getById(id),
                        "Report fetched successfully"
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CompetitiveBrandReport>>> getAll() {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        reportService.getAll(),
                        "Reports fetched successfully"
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.ok(
                ApiResponseDto.success(null, "Report deleted successfully")
        );
    }
}
