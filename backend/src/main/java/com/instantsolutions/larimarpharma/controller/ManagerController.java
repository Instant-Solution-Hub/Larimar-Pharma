package com.instantsolutions.larimarpharma.controller;


import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.DashboardStatsDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerRequestDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerResponseDto;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.service.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<Manager>> create(@Valid @RequestBody ManagerRequestDto dto) {
        Manager saved = managerService.createManager(dto);
        return new ResponseEntity<>(
                ApiResponseDto.success(saved,"Manager created successfully"),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Manager>> update(
            @PathVariable Long id,
            @Valid  @RequestBody ManagerRequestDto request
    ) {
        Manager updated = managerService.updateManager(id, request);
        return ResponseEntity.ok(
                ApiResponseDto.success(updated,"Manager updated successfully" )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ManagerResponseDto>> getById(@PathVariable Long id) {
        ManagerResponseDto manager = managerService.getManagerById(id);
        return ResponseEntity.ok(
                ApiResponseDto.success(manager,"Manager fetched successfully")
        );
    }

    @GetMapping("fetch-fieldexecutives/{id}")
    public ResponseEntity<ApiResponseDto<ManagerResponseDto>> getFieldExecutivesByManagerId(@PathVariable Long id) {
        ManagerResponseDto manager = managerService.getManagerById(id);
        return ResponseEntity.ok(
                ApiResponseDto.success(manager,"Manager fetched successfully")
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ManagerResponseDto>>> getAll() {
        List<ManagerResponseDto> managers = managerService.getAllManagers();
        return ResponseEntity.ok(
                ApiResponseDto.success(managers,"Managers fetched successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {
        managerService.deleteManager(id);
        return ResponseEntity.ok(
                ApiResponseDto.success(null,"Manager deleted successfully")
        );
    }

//    @GetMapping("stats/{id}")
//    public ResponseEntity<ApiResponseDto<DashboardStatsDto>> getDashboardStats(@PathVariable Long id) {
//        DashboardStatsDto manager = managerService.getDashboardStats(id);
//        return ResponseEntity.ok(
//                ApiResponseDto.success(manager,"Manager fetched successfully")
//        );
//    }

}
