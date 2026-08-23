// SlotPlanningDayRequestService.java
package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.ApprovedSlotPlanRequestCheckDto;
import com.instantsolutions.larimarpharma.DTOs.SlotPlanningDayRequestDto;
import com.instantsolutions.larimarpharma.DTOs.SlotPlanningDayRequestResponseDto;
import com.instantsolutions.larimarpharma.DTOs.SlotPlanningDayRequestUpdateDto;
import com.instantsolutions.larimarpharma.entity.Admin;
import com.instantsolutions.larimarpharma.entity.SlotPlanningDayRequest;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.repository.AdminRepository;
import com.instantsolutions.larimarpharma.repository.SlotPlanningDayRequestRepository;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotPlanningDayRequestService {

    private final SlotPlanningDayRequestRepository requestRepository;
    private final ManagerRepository managerRepository;
    private final AdminRepository adminRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;

    @Transactional
    public SlotPlanningDayRequestResponseDto createRequest(SlotPlanningDayRequestDto requestDto, String requesterType, Long requesterId) {
        SlotPlanningDayRequest request = new SlotPlanningDayRequest();
        request.setReason(requestDto.getReason());
        request.setRequestedAt(LocalDate.now());

        // Set the requester based on type
        if ("MANAGER".equalsIgnoreCase(requesterType)) {
            Manager manager = managerRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + requesterId));
            request.setRequestedManager(manager);

            // Check for existing pending request
            if (requestRepository.existsByRequestedManagerAndStatusAndRequestedAt(
                    manager, SlotPlanningDayRequest.RequestStatus.PENDING, request.getRequestedAt())) {
                throw new RuntimeException("A pending request already exists for this date");
            }
        } else if ("FE".equalsIgnoreCase(requesterType)) {
            FieldExecutive fe = fieldExecutiveRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("Field Executive not found with id: " + requesterId));
            request.setRequestedFieldExecutive(fe);

            // Check for existing pending request
            if (requestRepository.existsByRequestedFieldExecutiveAndStatusAndRequestedAt(
                    fe, SlotPlanningDayRequest.RequestStatus.PENDING, request.getRequestedAt())) {
                throw new RuntimeException("A pending request already exists for this date");
            }
        }else if ("ZSM".equalsIgnoreCase(requesterType)){
            Admin zsmAdmin = adminRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("ZSM not found with id: " + requesterId));
            request.setRequestedZsm(zsmAdmin);

            // Check for existing pending request
            if (requestRepository.existsByRequestedZsmAndStatusAndRequestedAt(
                    zsmAdmin, SlotPlanningDayRequest.RequestStatus.PENDING, request.getRequestedAt())) {
                throw new RuntimeException("A pending request already exists for this date");
            }
        }
        else {
            throw new RuntimeException("Invalid requester type");
        }

        SlotPlanningDayRequest savedRequest = requestRepository.save(request);
        return mapToResponseDto(savedRequest);
    }

    @Transactional
    public SlotPlanningDayRequestResponseDto updateRequestStatus(Long requestId, SlotPlanningDayRequestUpdateDto updateDto, Long adminId) {
        SlotPlanningDayRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        // Verify admin exists (you can add admin validation logic)
        // Admin admin = adminRepository.findById(adminId)
        //     .orElseThrow(() -> new RuntimeException("Admin not found"));

        request.setStatus(updateDto.getStatus());
        request.setAdminNotes(updateDto.getAdminNotes());

        // reviewedAt will be set automatically by @PreUpdate

        SlotPlanningDayRequest updatedRequest = requestRepository.save(request);
        return mapToResponseDto(updatedRequest);
    }

    @Transactional(readOnly = true)
    public SlotPlanningDayRequestResponseDto getRequestById(Long requestId) {
        SlotPlanningDayRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));
        return mapToResponseDto(request);
    }

    @Transactional(readOnly = true)
    public List<SlotPlanningDayRequestResponseDto> getRequestsByManager(Long managerId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));

        return requestRepository.findByRequestedManager(manager).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SlotPlanningDayRequestResponseDto> getRequestsByZsm(Long zsmId) {
        Admin zsmAdmin = adminRepository.findById(zsmId)
                .orElseThrow(() -> new RuntimeException("ZSM not found with id: " + zsmId));

        return requestRepository.findByRequestedZsm(zsmAdmin).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SlotPlanningDayRequestResponseDto> getRequestsByFieldExecutive(Long feId) {
        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new RuntimeException("Field Executive not found with id: " + feId));

        return requestRepository.findByRequestedFieldExecutive(fe).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SlotPlanningDayRequestResponseDto> getAllRequestsForAdmin() {
        return requestRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SlotPlanningDayRequestResponseDto> getPendingRequests() {
        return requestRepository.findByStatus(SlotPlanningDayRequest.RequestStatus.PENDING).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelRequest(Long requestId, Long requesterId, String requesterType) {
        SlotPlanningDayRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        // Verify ownership
        if ("MANAGER".equalsIgnoreCase(requesterType)) {
            if (request.getRequestedManager() == null || !request.getRequestedManager().getId().equals(requesterId)) {
                throw new RuntimeException("You can only cancel your own requests");
            }
        } else if ("FE".equalsIgnoreCase(requesterType)) {
            if (request.getRequestedFieldExecutive() == null || !request.getRequestedFieldExecutive().getId().equals(requesterId)) {
                throw new RuntimeException("You can only cancel your own requests");
            }
        } else {
            throw new RuntimeException("Invalid requester type");
        }

        if (request.getStatus() != SlotPlanningDayRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be cancelled");
        }

        request.setStatus(SlotPlanningDayRequest.RequestStatus.CANCELLED);
        requestRepository.save(request);
    }

    private SlotPlanningDayRequestResponseDto mapToResponseDto(SlotPlanningDayRequest request) {
        SlotPlanningDayRequestResponseDto dto = new SlotPlanningDayRequestResponseDto();
        dto.setId(request.getId());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setReviewedAt(request.getReviewedAt());
        dto.setAdminNotes(request.getAdminNotes());

        if (request.getRequestedManager() != null) {
            dto.setRequestedManagerId(request.getRequestedManager().getId());
            dto.setRequestedManagerName(request.getRequestedManager().getName()); // Assuming Manager has getName()
        }

        if (request.getRequestedZsm() != null) {
            dto.setRequestedManagerId(request.getRequestedZsm().getId());
            dto.setRequestedManagerName(request.getRequestedZsm().getName()); // Assuming Manager has getName()
        }

        if (request.getRequestedFieldExecutive() != null) {
            dto.setRequestedFieldExecutiveId(request.getRequestedFieldExecutive().getId());
            dto.setRequestedFieldExecutiveName(request.getRequestedFieldExecutive().getName()); // Assuming FE has getName()
        }

        return dto;
    }

    // Add to SlotPlanningDayRequestService.java

    @Transactional(readOnly = true)
    public ApprovedSlotPlanRequestCheckDto checkApprovedRequestForManagerToday(Long managerId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));

        LocalDate today = LocalDate.now();

        Optional<SlotPlanningDayRequest> approvedRequest = requestRepository
                .findByRequestedManagerAndStatusAndRequestedAt(
                        manager,
                        SlotPlanningDayRequest.RequestStatus.APPROVED,
                        today
                );

        if (approvedRequest.isPresent()) {
            SlotPlanningDayRequest request = approvedRequest.get();
            return ApprovedSlotPlanRequestCheckDto.builder()
                    .hasApprovedRequest(true)
                    .currentDate(today)
                    .requestId(request.getId())
                    .reason(request.getReason())
                    .requesterType("MANAGER")
                    .requesterName(manager.getName())
                    .message("You have an approved request for today. You can plan your slot.")
                    .build();
        } else {
            return ApprovedSlotPlanRequestCheckDto.builder()
                    .hasApprovedRequest(false)
                    .currentDate(today)
                    .message("No approved request found for today.")
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public ApprovedSlotPlanRequestCheckDto checkApprovedRequestForFieldExecutiveToday(Long feId) {
        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new RuntimeException("Field Executive not found with id: " + feId));

        LocalDate today = LocalDate.now();

        Optional<SlotPlanningDayRequest> approvedRequest = requestRepository
                .findByRequestedFieldExecutiveAndStatusAndRequestedAt(
                        fe,
                        SlotPlanningDayRequest.RequestStatus.APPROVED,
                        today
                );

        if (approvedRequest.isPresent()) {
            SlotPlanningDayRequest request = approvedRequest.get();
            return ApprovedSlotPlanRequestCheckDto.builder()
                    .hasApprovedRequest(true)
                    .currentDate(today)
                    .requestId(request.getId())
                    .reason(request.getReason())
                    .requesterType("FIELD_EXECUTIVE")
                    .requesterName(fe.getName())
                    .message("You have an approved request for today. You can plan your slot.")
                    .build();
        } else {
            return ApprovedSlotPlanRequestCheckDto.builder()
                    .hasApprovedRequest(false)
                    .currentDate(today)
                    .message("No approved request found for today.")
                    .build();
        }
    }

    // Optional: Combined method for checking both types
    @Transactional(readOnly = true)
    public ApprovedSlotPlanRequestCheckDto checkApprovedRequestToday(String requesterType, Long requesterId) {
        if ("MANAGER".equalsIgnoreCase(requesterType)) {
            return checkApprovedRequestForManagerToday(requesterId);
        } else if ("FE".equalsIgnoreCase(requesterType) || "FIELD_EXECUTIVE".equalsIgnoreCase(requesterType)) {
            return checkApprovedRequestForFieldExecutiveToday(requesterId);
        } else {
            throw new RuntimeException("Invalid requester type");
        }
    }

    // Optional: Method to check if user can plan slot (returns boolean only)
    @Transactional(readOnly = true)
    public boolean canPlanSlotToday(String requesterType, Long requesterId) {
        if ("MANAGER".equalsIgnoreCase(requesterType)) {
            Manager manager = managerRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            return requestRepository.hasApprovedRequestForManager(
                    manager,
                    SlotPlanningDayRequest.RequestStatus.APPROVED,
                    LocalDate.now()
            );
        }else if ("ZSM".equalsIgnoreCase(requesterType)) {
            Admin zsmAdmin = adminRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("Field Executive not found"));
            return requestRepository.hasApprovedRequestForZsm(
                    zsmAdmin,
                    SlotPlanningDayRequest.RequestStatus.APPROVED,
                    LocalDate.now()
            );
        }
        else if ("FE".equalsIgnoreCase(requesterType)) {
            FieldExecutive fe = fieldExecutiveRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("Field Executive not found"));
            return requestRepository.hasApprovedRequestForFieldExecutive(
                    fe,
                    SlotPlanningDayRequest.RequestStatus.APPROVED,
                    LocalDate.now()
            );
        }
        return false;
    }
}