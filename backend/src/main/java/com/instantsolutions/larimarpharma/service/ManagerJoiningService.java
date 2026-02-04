package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.ManagerJoiningRequestDto;
import com.instantsolutions.larimarpharma.DTOs.ManagerJoiningResponse2Dto;
import com.instantsolutions.larimarpharma.DTOs.ManagerJoiningResponse3Dto;
import com.instantsolutions.larimarpharma.DTOs.ManagerJoiningResponseDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class  ManagerJoiningService {

    @Autowired
    ManagerJoiningRepository repository;
    @Autowired
    FieldExecutiveRepository fieldExecutiveRepository;
    @Autowired
    ManagerRepository managerRepository;
    @Autowired
    DoctorRepository doctorRepository;


    @Transactional
    public ManagerJoiningResponseDto create(ManagerJoiningRequestDto dto) {

        FieldExecutive fe = fieldExecutiveRepository.findById(dto.getFieldExecutiveId())
                .orElseThrow(() -> new ResourceNotFoundException("Field Executive not found"));

        Manager manager = managerRepository.findById(fe.getManager().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        ManagerJoining joining = ManagerJoining.builder()
                .fieldExecutive(fe)
                .manager(manager)
                .doctor(doctor)
                .scheduledTime(dto.getScheduledTime())
                .actualJoiningTime(dto.getActualJoiningTime())
                .status(dto.getStatus() == null ? null :
                        ManagerJoining.JoiningStatus.valueOf(dto.getStatus().name()))
                .notes(dto.getNotes())
                .build();

        repository.save(joining);
        return mapToResponse(joining);
    }


    public ManagerJoiningResponseDto update(Long id, ManagerJoiningRequestDto dto) {

        ManagerJoining joining = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager joining not found"));

        joining.setScheduledTime(dto.getScheduledTime());
        joining.setActualJoiningTime(dto.getActualJoiningTime());
        joining.setNotes(dto.getNotes());

        if (dto.getStatus() != null) {
            joining.setStatus(ManagerJoining.JoiningStatus.valueOf(dto.getStatus().name()));
        }

        repository.save(joining);
        return mapToResponse(joining);
    }


    public ManagerJoiningResponseDto getById(Long id) {
        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Manager joining not found"));
    }


    public List<ManagerJoiningResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<ManagerJoiningResponse2Dto> getCurrentMonthByManagerId(Long managerId) {

        LocalDate now = LocalDate.now();

        return repository.findCurrentMonthByManagerId(
                        managerId,
                        now.getMonthValue(),
                        now.getYear()
                )
                .stream()
                .map(joining -> ManagerJoiningResponse2Dto.builder()
                        .id(joining.getId())
                        .feName(joining.getFieldExecutive().getName())
                        .date(joining.getCreatedAt())
                        .scheduledTime(joining.getScheduledTime())
                        .joiningTime(joining.getActualJoiningTime())
                        .status(joining.getStatus().name())
                        .feId(joining.getFieldExecutive().getId())
                        .doctorName(joining.getDoctor().getName())
                        .hospital(joining.getDoctor().getHospitalName())
                        .build()
                )
                .toList();
    }



    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Manager joining not found");
        }
        repository.deleteById(id);
    }


    public List<ManagerJoiningResponseDto> getForFeAndMonth(Long feId, int month, int year) {

        if (feId == null) {
            throw new IllegalArgumentException("Field Executive ID is required");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        // Optional: Validate FE exists
        fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new ResourceNotFoundException("Field Executive not found"));

        return repository
                .findByFeAndMonth(feId, month, year)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ManagerJoiningResponseDto mapToResponse(ManagerJoining entity) {
        return ManagerJoiningResponseDto.builder()
                .id(entity.getId())
                .fieldExecutiveId(entity.getFieldExecutive().getId())
                .managerId(entity.getManager().getId())
                .doctorName(entity.getDoctor().getName())
                .hospitalName(entity.getDoctor().getHospitalName())
                .scheduledTime(entity.getScheduledTime())
                .actualJoiningTime(entity.getActualJoiningTime())
                .status(entity.getStatus() == null ? null : entity.getStatus().name())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Transactional
    public List<ManagerJoiningResponse3Dto> getCurrentMonthJoinings() {
        LocalDate now = LocalDate.now();
        return repository.findCurrentMonth( now.getMonthValue() , now.getYear())
                .stream()
                .map(joining -> ManagerJoiningResponse3Dto.builder()
                        .id(joining.getId())
                        .feName(joining.getFieldExecutive().getName())
                        .date(joining.getCreatedAt())
                        .scheduledTime(joining.getScheduledTime())
                        .joiningTime(joining.getActualJoiningTime())
                        .status(joining.getStatus().name())
                        .feId(joining.getFieldExecutive().getId())
                        .doctorName(joining.getDoctor().getName())
                        .hospital(joining.getDoctor().getHospitalName())
                        .managerName(joining.getManager().getName())
                        .build()
                )
                .toList();
    }
}
