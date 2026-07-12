package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.*;
import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.entity.DoctorChangeRequest;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.repository.DoctorChangeRequestRepository;
import com.instantsolutions.larimarpharma.repository.DoctorRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class DoctorChangeRequestService {

    private final DoctorChangeRequestRepository requestRepository;
    private final DoctorRepository doctorRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;


    public DoctorChangeRequestResponseDto createRequest(
            Long feId,
            DoctorChangeRequestDto dto
    ) {

        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() ->
                        new RuntimeException("Field Executive not found"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found"));

        if (!doctor.getFieldExecutive().getId().equals(feId)) {
            throw new RuntimeException(
                    "Doctor is not assigned to this Field Executive");
        }

        boolean alreadyPending =
                requestRepository.findByFieldExecutiveIdAndStatus(
                                feId,
                                DoctorChangeRequest.RequestStatus.PENDING
                        )
                        .stream()
                        .anyMatch(r -> r.getDoctor().getId().equals(dto.getDoctorId()));

        if (alreadyPending) {
            throw new RuntimeException(
                    "A pending request already exists for this doctor.");
        }

        if (doctor.getPracticeType() == dto.getRequestedPracticeType()) {
            throw new RuntimeException(
                    "Doctor already belongs to this practice type.");
        }

        DoctorChangeRequest request =
                DoctorChangeRequest.builder()
                        .doctor(doctor)
                        .fieldExecutive(fe)
                        .currentPracticeType(doctor.getPracticeType())
                        .requestedPracticeType(dto.getRequestedPracticeType())
                        .build();

        requestRepository.save(request);

        return mapToDto(request);
    }


    @Transactional
    public List<DoctorChangeRequestResponseDto> getAllRequests() {

        return requestRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();

    }


    @Transactional
    public List<DoctorChangeRequestResponseDto> getRequestsByFE(Long feId) {

        return requestRepository.findByFieldExecutiveId(feId)
                .stream()
                .map(this::mapToDto)
                .toList();

    }


    public DoctorChangeRequestResponseDto reviewRequest(
            Long requestId,
            DoctorChangeReviewRequestDto dto
    ) {

        DoctorChangeRequest request =
                requestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new RuntimeException("Request not found"));

        if (request.getStatus() !=
                DoctorChangeRequest.RequestStatus.PENDING) {

            throw new RuntimeException(
                    "This request has already been reviewed.");
        }

        request.setStatus(dto.getStatus());
        request.setReviewedAt(LocalDateTime.now());



            if (dto.getStatus() == DoctorChangeRequest.RequestStatus.APPROVED) {

                Doctor doctor = request.getDoctor();

                Doctor.PracticeType currentType = request.getCurrentPracticeType();
                Doctor.PracticeType requestedType = request.getRequestedPracticeType();

                doctor.setPracticeType(requestedType);

                FieldExecutive fe = request.getFieldExecutive();

                int current = getPriority(currentType);
                int requested = getPriority(requestedType);

                if (requested < current) {
                    fe.setUpgradedCount(fe.getUpgradedCount() + 1);
                } else if (requested > current) {
                    fe.setDowngradedCount(fe.getDowngradedCount() + 1);
                }

                doctorRepository.save(doctor);
                fieldExecutiveRepository.save(fe);
            }


        requestRepository.save(request);

        return mapToDto(request);
    }




    @Transactional
    public List<DoctorChangeStatsDto> getAllFEDoctorChangeStats() {

        return fieldExecutiveRepository.findAll()
                .stream()
                .map(fe -> DoctorChangeStatsDto.builder()
                        .feId(fe.getId())
                        .feName(fe.getName())
                        .upgraded(fe.getUpgradedCount())
                        .downgraded(fe.getDowngradedCount())
                        .build())
                .toList();
    }



    @Transactional
    public DoctorChangeStatsDto getStats(Long feId) {

        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new RuntimeException("Field Executive not found"));

        return DoctorChangeStatsDto.builder()
                .feName(fe.getName())
                .feId(fe.getId())
                .upgraded(fe.getUpgradedCount())
                .downgraded(fe.getDowngradedCount())
                .build();
    }

    private int getPriority(Doctor.PracticeType type) {

        return switch (type) {

            case RP -> 1;

            case OP -> 2;

            case NP -> 3;
        };



    }


    private DoctorChangeRequestResponseDto mapToDto(
            DoctorChangeRequest request
    ) {

        return DoctorChangeRequestResponseDto.builder()
                .id(request.getId())

                .doctorId(request.getDoctor().getId())
                .doctorName(request.getDoctor().getName())

                .fieldExecutiveId(request.getFieldExecutive().getId())
                .fieldExecutiveName(request.getFieldExecutive().getName())

                .currentPracticeType(
                        request.getCurrentPracticeType().name())

                .requestedPracticeType(
                        request.getRequestedPracticeType().name())

                .status(request.getStatus().name())

                .requestedAt(request.getRequestedAt())

                .reviewedAt(request.getReviewedAt())

                .build();
    }


    @Transactional
    public List<DoctorChangeRequestResponseDto> getAllPendingRequests() {

        return requestRepository
                .findByStatus(DoctorChangeRequest.RequestStatus.PENDING)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

}