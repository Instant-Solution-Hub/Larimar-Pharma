package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.PharmacyRequestDto;
import com.instantsolutions.larimarpharma.DTOs.PharmacyResponseDto;
import com.instantsolutions.larimarpharma.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
@CrossOrigin
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @PostMapping
    public ResponseEntity<PharmacyResponseDto> create(
            @RequestBody PharmacyRequestDto dto
    ) {
        return new ResponseEntity<>(pharmacyService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PharmacyResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<PharmacyResponseDto>> getAll() {
        return ResponseEntity.ok(pharmacyService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PharmacyResponseDto> update(
            @PathVariable Long id,
            @RequestBody PharmacyRequestDto dto
    ) {
        return ResponseEntity.ok(pharmacyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pharmacyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
