package com.instantsolutions.larimarpharma.service;



import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.FieldExecutiveProfile;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.DoctorRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveProfileRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FEService {

    private final FieldExecutiveRepository repository;
    private final ManagerRepository managerRepository;
    private final DoctorRepository doctorRepository;
    private final FieldExecutiveProfileRepository profileRepository;

    // CREATE
    public FieldExecutiveResponse create(FieldExecutiveRequest request) {

        LocalDate now = LocalDate.now();

        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        // Uniqueness checks
        if (repository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new RuntimeException("Employee code already exists");
        }

        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (repository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }
        Manager manager = null;
        if (request.getManagerId() != null && request.getManagerId() > 0) {
            manager = managerRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
        }

        FieldExecutive fe = FieldExecutive.builder()
                .name(request.getName().toUpperCase())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(request.getPassword())
                .employeeCode(request.getEmployeeCode().toUpperCase())
                .territory(request.getTerritory())
                .region(request.getRegion())
                .manager(manager)
                .markets( request.getMarkets() != null
                        ? new ArrayList<>(request.getMarkets())
                        : new ArrayList<>())
                .active(true).build();

        FieldExecutiveProfile profile = FieldExecutiveProfile.builder()
                .fieldExecutive(fe)
                .attendancePercentage(0)
                .primaryTargetAchieved(0.0)
                .secondaryTargetAchieved(0.0)
                .primaryTargetSet(100.0)
                .secondaryTargetSet(100.0)
                .incentiveEarned(0.0)
                .casualLeaves(10)
                .sickLeaves(10)
                .approvedCasualLeaves(0)
                .approvedSickLeaves(0)
                .pharmacyVisitProgress(0)
                .stockistVisitProgress(0)
                .doctorVisitProgress(0)
                .aPlusDoctorTarget(90)
                .bDoctorTarget(30)
                .aDoctorTarget(60)
                .month(now.getMonthValue())   // 1–12
                .year(now.getYear())
                .build();

        fe.setProfile(profile); // IMPORTANT

        FieldExecutive saved = repository.save(fe); // profile auto-saved
        return mapToResponse(saved);
    }

    // READ ALL
    @Transactional
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

        // Email uniqueness check
        if (!fe.getEmail().equals(request.getEmail())
                && repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Phone uniqueness check
        if (!fe.getPhone().equals(request.getPhone())
                && repository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already in use");
        }

        Manager manager = null;
        if (request.getManagerId() != null) {
            manager = managerRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
        }

        fe.setName(request.getName().trim().toUpperCase());
        fe.setEmail(request.getEmail().toLowerCase());
        fe.setPassword(request.getPassword());
        fe.setPhone(request.getPhone());
        fe.setTerritory(request.getTerritory());
        fe.setRegion(request.getRegion());
        fe.setManager(manager);
        fe.setMarkets(request.getMarkets());

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
    public FieldExecutiveResponse mapToResponse(FieldExecutive fe) {
        System.out.println("Markets: " + fe.getMarkets());

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
                .managerName( fe.getManager() != null ? fe.getManager().getName() : null)
                .markets(
                        fe.getMarkets() != null ? fe.getMarkets() : Collections.emptyList()
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

    @Transactional
    public FEContactResponseDto updateContactDetails(
            Long feId,
            FEContactUpdateRequestDto dto
    ) {
        FieldExecutive fe = repository.findById(feId)
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        if (dto.getPhone() != null) {
            fe.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            fe.setEmail(dto.getEmail());
        }
        if (dto.getEmergencyContact() != null) {
            fe.setEmergencyContact(dto.getEmergencyContact());
        }

        FieldExecutive updated = repository.save(fe);

        return FEContactResponseDto.builder()
                .feId(updated.getId())
                .phone(updated.getPhone())
                .email(updated.getEmail())
                .emergencyContact(updated.getEmergencyContact())
                .name(updated.getName())
                .build();
    }

    @Transactional(readOnly = true)
    public FEProfileStatsResponseDto getProfileStats(Long feId) {

        FieldExecutiveProfile profile = profileRepository
                .findByFieldExecutiveId(feId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found for FE ID: " + feId)
                );

        return FEProfileStatsResponseDto.builder()
                .targetAchieved(profile.getPrimaryTargetAchieved())
                .casualLeaves(profile.getCasualLeaves())
                .approvedCasualLeaves(profile.getApprovedCasualLeaves())
                .sickLeaves(profile.getSickLeaves())
                .approvedSickLeaves(profile.getApprovedSickLeaves())
                .targetSet(profile.getPrimaryTargetSet())
                .build();
    }

    public FEContactResponseDto getContactDetails(Long feId) {

        FieldExecutive fe = repository.findById(feId)
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        return  FEContactResponseDto.builder()
                .email(fe.getEmail()).
                emergencyContact(fe.getEmergencyContact()).
                phone(fe.getPhone()).name(fe.getName()).
                build();

    }


    public List<FEContactResponseDto> getAllContactDetails() {

        return repository.findAll()
                .stream()
                .map(fe -> FEContactResponseDto.builder()
                        .name(fe.getName())
                        .email(fe.getEmail())
                        .phone(fe.getPhone())
                        .emergencyContact(fe.getEmergencyContact())
                        .build()
                )
                .toList();
    }


    public List<DoctorResponseDto> getAllocatedDoctors(Long feId) {

        if (!repository.existsById(feId)) {
            throw new ResourceNotFoundException("Field Executive not found");
        }


        // Option 1: Using repository query (recommended)
        List<Doctor> doctors = doctorRepository.findByFieldExecutiveId(feId);

        return doctors.stream()
                .map(DoctorResponseDto::fromEntity)
                .toList();
    }



    @Transactional(readOnly = true)
    public List<FieldExecutiveResponse> getFEsWithAPriorityVisits(
            Long managerId,
            Integer weekNumber,
            Integer dayOfWeek
    ) {
        List<FieldExecutive> fieldExecutives = repository
                .findFEsWithScheduledAPriorityDoctorVisits(
                        managerId,
                        weekNumber,
                        dayOfWeek
                );
        return  fieldExecutives.stream().map(this::mapToResponse).toList();
    }



    @Transactional
    public FETargetResponseDto assignMonthlyTarget(
            Long feId,
            AssignFETargetRequestDto dto
    ) {
        FieldExecutive fe = repository.findById(feId)
                .orElseThrow(() ->
                        new RuntimeException("Field Executive not found"));

        FieldExecutiveProfile profile =
                profileRepository
                        .findByFieldExecutiveIdAndMonthAndYear(
                                feId, dto.getMonth(), dto.getYear()
                        )
                        .orElseGet(() ->
                                FieldExecutiveProfile.builder()
                                        .fieldExecutive(fe)
                                        .month(dto.getMonth())
                                        .year(dto.getYear())
                                        .primaryTargetSet(0.0)
                                        .secondaryTargetSet(0.0)
                                        .primaryTargetAchieved(0.0)
                                        .secondaryTargetAchieved(0.0)
                                        .build()
                        );

        profile.setPrimaryTargetSet(dto.getPrimaryTargetSet());
        profile.setSecondaryTargetSet(dto.getSecondaryTargetSet());

        profileRepository.save(profile);

        return FETargetResponseDto.builder()
                .feId(fe.getId())
                .feName(fe.getName())
                .territory(fe.getTerritory())
                .primaryTarget(profile.getPrimaryTargetSet())
                .secondaryTarget(profile.getSecondaryTargetSet())
                .month(profile.getMonth())
                .year(profile.getYear())
                .build();
    }


    @Transactional
    public List<FEMonthlyTargetResponseDto> getFEMonthlyTargets(
            Long managerId,
            Integer month,
            Integer year
    ) {
        List<FieldExecutive> fes =
                repository.findByManagerId(managerId);

        for (FieldExecutive fe : fes) {
            profileRepository
                    .findByFieldExecutiveIdAndMonthAndYear(
                            fe.getId(), month, year
                    )
                    .orElseGet(() -> {
                        FieldExecutiveProfile profile =
                                FieldExecutiveProfile.builder()
                                        .fieldExecutive(fe)
                                        .month(month)
                                        .year(year)
                                        .primaryTargetSet(0.0)
                                        .secondaryTargetSet(0.0)
                                        .primaryTargetAchieved(0.0)
                                        .secondaryTargetAchieved(0.0)
                                        .attendancePercentage(0)
                                        .incentiveEarned(0.0)
                                        .casualLeaves(10)
                                        .sickLeaves(10)
                                        .approvedCasualLeaves(0)
                                        .approvedSickLeaves(0)
                                        .pharmacyVisitProgress(0)
                                        .stockistVisitProgress(0)
                                        .doctorVisitProgress(0)
                                        .aPlusDoctorTarget(0)
                                        .aDoctorTarget(0)
                                        .bDoctorTarget(0)
                                        .build();

                        return profileRepository.save(profile);

                    });
        }

        return profileRepository.findFEMonthlyTargets(managerId, month, year);
    }



}

