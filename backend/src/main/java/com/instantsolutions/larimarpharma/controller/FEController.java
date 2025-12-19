package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.ApiResponseDto;
import com.instantsolutions.larimarpharma.DTOs.FieldExecutiveRequest;
import com.instantsolutions.larimarpharma.DTOs.FieldExecutiveResponse;
import com.instantsolutions.larimarpharma.DTOs.LoginResponseDto;
import com.instantsolutions.larimarpharma.service.FEService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/field-executives")
@RequiredArgsConstructor
public class FEController {
    private final FEService service;

    @PostMapping
    public ResponseEntity<ApiResponseDto<FieldExecutiveResponse>> create(@RequestBody FieldExecutiveRequest request) {
        FieldExecutiveResponse response = service.create(request);
        return ResponseEntity.ok(
                ApiResponseDto.success(response, "Login successful")
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
    public FieldExecutiveResponse update(
            @PathVariable Long id,
            @RequestBody FieldExecutiveRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
