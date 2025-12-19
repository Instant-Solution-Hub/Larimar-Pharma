package com.instantsolutions.larimarpharma.service;



import com.instantsolutions.larimarpharma.DTOs.FEUpdateContactDto;
import com.instantsolutions.larimarpharma.DTOs.FieldExecutiveRequest;
import com.instantsolutions.larimarpharma.DTOs.FieldExecutiveResponse;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FEService {

    private final FieldExecutiveRepository repository;
    private final ManagerRepository managerRepository;

    // CREATE
    public FieldExecutiveResponse create(FieldExecutiveRequest request) {

        if (repository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new RuntimeException("Employee code already exists");
        }

        Manager manager = null;
        if (request.getManagerId() != null && request.getManagerId() > 0) {
            manager = managerRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
        }

        FieldExecutive fe = FieldExecutive.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(request.getPassword())
                .employeeCode(request.getEmployeeCode())
                .territory(request.getTerritory())
                .region(request.getRegion())
                .manager(manager)
                .build();

        return mapToResponse(repository.save(fe));
    }

    // READ ALL
    public List<FieldExecutiveResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // READ BY ID
    public FieldExecutiveResponse getById(Long id) {
        return mapToResponse(
                repository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Field Executive not found"))
        );
    }

    // UPDATE
    public FieldExecutiveResponse update(Long id, FieldExecutiveRequest request) {

        FieldExecutive fe = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        Manager manager = null;
        if (request.getManagerId() != null) {
            manager = managerRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
        }

        fe.setName(request.getName());
        fe.setEmail(request.getEmail());
        fe.setPhone(request.getPhone());
        fe.setTerritory(request.getTerritory());
        fe.setRegion(request.getRegion());
        fe.setManager(manager);

        return mapToResponse(repository.save(fe));
    }

    // DELETE
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Field Executive not found");
        }
        repository.deleteById(id);
    }

    // Mapper
    private FieldExecutiveResponse mapToResponse(FieldExecutive fe) {
        return FieldExecutiveResponse.builder()
                .id(fe.getId())
                .name(fe.getName())
                .email(fe.getEmail())
                .phone(fe.getPhone())
                .employeeCode(fe.getEmployeeCode())
                .territory(fe.getTerritory())
                .region(fe.getRegion())
                .managerId(
                        fe.getManager() != null ? fe.getManager().getId() : null
                )
                .build();
    }

    @Transactional
    public FieldExecutive updateContactDetails(
            Long feId,
            FEUpdateContactDto dto
    ) {
        FieldExecutive fe = repository.findById(feId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Field Executive not found"));

        // Email uniqueness check
        if (!fe.getEmail().equals(dto.getEmail())
                && repository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        if (dto.getPhone() != null &&
                dto.getPhone().equals(dto.getEmergencyContact())) {
            throw new BadRequestException(
                    "Phone number and emergency contact cannot be same"
            );
        }


        fe.setEmail(dto.getEmail());
        fe.setPhone(dto.getPhone());
        fe.setEmergencyContact(dto.getEmergencyContact());

        return repository.save(fe);
    }
}

