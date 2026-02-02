package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ManagerJoiningRequestDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerJoiningResponse2Dto;
import com.instantsolutions.larimarpharma.DTOs.ManagerJoiningResponseDto;
import com.instantsolutions.larimarpharma.service.ManagerJoiningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager-joinings")
@RequiredArgsConstructor
public class ManagerJoiningController {

    private final ManagerJoiningService service;

    @PostMapping
    public ResponseEntity<ManagerJoiningResponseDto> create(
            @RequestBody @Valid ManagerJoiningRequestDto dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManagerJoiningResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid ManagerJoiningRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/manager/{managerId}/current-month")
    public ResponseEntity<List<ManagerJoiningResponse2Dto>> getCurrentMonthJoiningsForManager(
            @PathVariable Long managerId
    ) {
        return ResponseEntity.ok(
                service.getCurrentMonthByManagerId(managerId)
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ManagerJoiningResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ManagerJoiningResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fe/monthly")
    public ResponseEntity<List<ManagerJoiningResponseDto>> getJoiningsForFeAndMonth(
            @RequestParam Long feId,
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(
                service.getForFeAndMonth(feId, month, year)
        );
    }


}
