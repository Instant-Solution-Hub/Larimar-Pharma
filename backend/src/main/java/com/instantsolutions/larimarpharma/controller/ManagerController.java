package com.instantsolutions.larimarpharma.controller;


import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.TerritoryMonthlyTarget;
import com.instantsolutions.larimarpharma.repository.TerritoryMonthlyTargetRepository;
import com.instantsolutions.larimarpharma.service.ManagerService;
import com.instantsolutions.larimarpharma.service.TerritoryMonthlyTargetService;
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
    private final TerritoryMonthlyTargetService territoryTargetService;
    private final TerritoryMonthlyTargetRepository territoryTargetRepo;

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

    @GetMapping("/{managerId}/fe-progress/monthly")
    public ResponseEntity<ApiResponseDto<ManagerMonthlyFEProgressDto>>
    getMonthlyFEProgress(
            @PathVariable Long managerId,
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        managerService.getMonthlyFEProgress(
                                managerId, month, year
                        ),
                        "Monthly FE progress fetched successfully"
                )
        );
    }

    @GetMapping("/{managerId}/territory-targets")
    public ResponseEntity<ApiResponseDto<List<TerritoryMonthlyTargetResponseDto>>> getTerritoryTargets(
            @PathVariable Long managerId,
            @RequestParam int month,
            @RequestParam int year
    ) {


        return ResponseEntity.ok(
                ApiResponseDto.success(
                        territoryTargetService.initializeTerritoryTargets(
                                managerId, month, year
                        ),
                        "Territory targets fetched successfully"
                )
        );
    }

    @PutMapping("/territory-targets/{id}")
    public ResponseEntity<ApiResponseDto<TerritoryMonthlyTargetResponseDto>> updateTerritoryTarget(
            @PathVariable Long id,
            @RequestBody UpdateTerritoryTargetDto dto
    ) {

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        territoryTargetService.update(id, dto),
                        "Territory targets fetched successfully"
                )
        );
    }

    @GetMapping("/{managerId}/contact")
    public ResponseEntity<ApiResponseDto<ManagerContactResponseDto>> getContactDetails(
            @PathVariable Long managerId
    ) {
        ManagerContactResponseDto response =
                managerService.getContactDetails(managerId);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        response,
                        "Contact details fetched successfully"
                )
        );


    }

    @PutMapping("/{managerId}/contact/basic")
    public ResponseEntity<ApiResponseDto<ManagerContactResponseDto>> updateContactDetails(
            @PathVariable Long managerId,
            @RequestBody ManagerContactUpdateRequestDto dto
    ) {
        ManagerContactResponseDto updated =
                managerService.updateContactDetails(managerId, dto);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        updated,
                        "Contact details updated successfully"
                )
        );
    }

    @GetMapping("/{managerId}/field-executives/contacts")
    public ResponseEntity<ApiResponseDto<List<FEContactResponseDto>>> getFEContactsUnderManager(
            @PathVariable Long managerId
    ) {

        List<FEContactResponseDto> response =
                managerService.getFEContactsUnderManager(managerId);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        response,
                        "FE contacts fetched successfully"
                )
        );
    }



}
