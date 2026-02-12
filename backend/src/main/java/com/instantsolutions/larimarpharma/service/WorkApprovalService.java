package com.instantsolutions.larimarpharma.service;


import com.instantsolutions.larimarpharma.DTOs.WorkApprovalRequestDto;
import com.instantsolutions.larimarpharma.DTOs.WorkApprovalResponseDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ManagerRepository;
import com.instantsolutions.larimarpharma.repository.SuperAdminRepository;
import com.instantsolutions.larimarpharma.repository.WorkApprovalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkApprovalService {

    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final ManagerRepository managerRepository;
    private final WorkApprovalRepository approvalRepository;
    private final SuperAdminRepository adminRepository;

    @Transactional
    public WorkApprovalResponseDto raiseRequest(WorkApprovalRequestDto dto) {

        FieldExecutive fe = null;
        Manager manager = null;

        if (dto.getFieldExecutiveId() != null) {
            fe = fieldExecutiveRepository.findById(dto.getFieldExecutiveId())
                    .orElseThrow(() -> new RuntimeException("FE not found"));
        }

        if (dto.getManagerId() != null) {
            manager = managerRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
        }

        // Validation
        if (fe == null && manager == null) {
            throw new IllegalArgumentException("Either FE or Manager must raise request");
        }

        WorkApproval request = WorkApproval.builder()
                .workDate(dto.getWorkDate())
                .fieldExecutive(fe)
                .manager(manager)
                .status(ApprovalRequest.ApprovalStatus.PENDING)
                .decription(dto.getDescription())
                .build();

        approvalRepository.save(request);

        return mapToDto(request);
    }
    @Transactional
    public WorkApprovalResponseDto approveRequest(Long requestId, Long adminId) {

        WorkApproval request = approvalRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        SuperAdmin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        request.setStatus(ApprovalRequest.ApprovalStatus.APPROVED);
        request.setApprovedBy(admin);

        return mapToDto(request);
    }

    @Transactional
    public WorkApprovalResponseDto rejectRequest(Long requestId, Long adminId) {

        WorkApproval request = approvalRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        SuperAdmin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        request.setStatus(ApprovalRequest.ApprovalStatus.REJECTED);
        request.setApprovedBy(admin);


        return mapToDto(request);
    }

    @Transactional
    public List<WorkApprovalResponseDto> getCurrentMonthWorkApprovals() {

        LocalDate now = LocalDate.now();

        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<WorkApproval> workApprovals =  approvalRepository.findByWorkDateBetween(startOfMonth, endOfMonth);

        return workApprovals.stream()
                .map(this::mapToDto)
                .toList();

    }

    @Transactional
    public List<WorkApprovalResponseDto> getCurrentMonthWorkApprovals(
            Long fieldExecutiveId,
            Long managerId
    ) {

        if (fieldExecutiveId == null && managerId == null) {
            throw new IllegalArgumentException("Either fieldExecutiveId or managerId must be provided");
        }

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<WorkApproval> approvals;

        if (fieldExecutiveId != null) {
            approvals = approvalRepository.findByFieldExecutiveIdAndWorkDateBetween(
                    fieldExecutiveId,
                    startOfMonth,
                    endOfMonth
            );
        } else {
            approvals = approvalRepository.findByManagerIdAndWorkDateBetween(
                    managerId,
                    startOfMonth,
                    endOfMonth
            );
        }

        return approvals.stream()
                .map(this::mapToDto)
                .toList();
    }

    private WorkApprovalResponseDto mapToDto(WorkApproval request) {

        String requestedByName;
        String requestedByRole;

        if (request.getFieldExecutive() != null) {
            requestedByName = request.getFieldExecutive().getName();
            requestedByRole = "FIELD_EXECUTIVE";
        } else {
            requestedByName = request.getManager().getName();
            requestedByRole = "MANAGER";
        }

        return WorkApprovalResponseDto.builder()
                .id(request.getId())
                .workDate(request.getWorkDate())
                .requestedByName(requestedByName)
                .requestedByRole(requestedByRole)
                .status(request.getStatus())
                .description(request.getDecription())
                .build();
    }


}
