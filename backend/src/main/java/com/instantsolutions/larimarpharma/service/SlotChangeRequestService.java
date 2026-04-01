package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.SlotChangeRequestDto;
import com.instantsolutions.larimarpharma.DTOs.SlotChangeRequestResponse;
import com.instantsolutions.larimarpharma.DTOs.AdminReviewRequest;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.*;
import com.instantsolutions.larimarpharma.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.instantsolutions.larimarpharma.utils.DateUtil.calculateVisitDate;
import static com.instantsolutions.larimarpharma.utils.DateUtil.calculateVisitDateCurrentMonth;

@Service
@RequiredArgsConstructor
public class SlotChangeRequestService {

    private final SlotChangeRequestRepository requestRepository;
    private final VisitRepository visitRepository;
    private final ManagerVisitRepository managerVisitRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final ManagerRepository managerRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public SlotChangeRequestResponse createRequest(SlotChangeRequestDto dto, Long requesterId, String requesterType) {
        SlotChangeRequest request = new SlotChangeRequest();

        // Set visit or manager visit
        if (dto.getVisitId() != null) {
            Visit visit = visitRepository.findById(dto.getVisitId())
                    .orElseThrow(() -> new RuntimeException("Visit not found"));
            request.setVisit(visit);
            request.setCurrentVisitDate(visit.getVisitDate());
            request.setCurrentWeekNumber(visit.getWeekNumber());
            request.setCurrentDayOfWeek(visit.getDayOfWeek());

        } else if (dto.getManagerVisitId() != null) {
            ManagerVisit managerVisit = managerVisitRepository.findById(dto.getManagerVisitId())
                    .orElseThrow(() -> new RuntimeException("Manager visit not found"));
            request.setManagerVisit(managerVisit);
            request.setCurrentVisitDate(managerVisit.getVisitDate());
            request.setCurrentWeekNumber(managerVisit.getWeekNumber());
            request.setCurrentDayOfWeek(managerVisit.getDayOfWeek());
        } else {
            throw new RuntimeException("Either visitId or managerVisitId must be provided");
        }

        // Set requester based on type
        if ("MANAGER".equalsIgnoreCase(requesterType)) {
            Manager manager = managerRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            request.setRequestedManager(manager);

            // Validate manager can request this change
//            if (dto.getVisitId() != null && request.getVisit() != null) {
//                // Check if manager is authorized for this field executive's visit
//                // You might want to add specific business logic here
//            }

        } else if ("FIELD_EXECUTIVE".equalsIgnoreCase(requesterType)) {
            // For FieldExecutive, we need to check if the requester is the owner of the visit
            if (dto.getVisitId() == null) {
                throw new RuntimeException("Field Executive can only request changes for regular visits");
            }

            FieldExecutive fieldExecutive = fieldExecutiveRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("Field Executive not found"));

            Visit visit = request.getVisit();
            if (!visit.getFieldExecutive().getId().equals(requesterId)) {
                throw new RuntimeException("Field Executive not authorized to modify this visit");
            }

            // Note: You'll need to update the SlotChangeRequest entity to include requestedFieldExecutive field
            // Add this to your entity: @ManyToOne private FieldExecutive requestedFieldExecutive;
             request.setRequestedFieldExecutive(fieldExecutive);
        } else {
            throw new RuntimeException("Invalid requester type");
        }
        LocalDate today = LocalDate.now();
        if(today.getDayOfMonth() == 2){
            request.setRequestedVisitDate(calculateVisitDate(dto.getRequestedWeekNumber(),dto.getRequestedDayOfWeek() ));
        }else {
            request.setRequestedVisitDate(calculateVisitDateCurrentMonth(dto.getRequestedWeekNumber(),dto.getRequestedDayOfWeek() ));
        }



        request.setRequestedWeekNumber(dto.getRequestedWeekNumber());
        request.setRequestedDayOfWeek(dto.getRequestedDayOfWeek());
        request.setReason(dto.getReason());

        SlotChangeRequest entity = requestRepository.save(request);
        return convertToResponse(entity);
    }

    @Transactional
    public SlotChangeRequestResponse processAdminReview(Long requestId, AdminReviewRequest reviewRequest, Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        SlotChangeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != SlotChangeRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        if ("APPROVE".equalsIgnoreCase(reviewRequest.getAction())) {
            request.setStatus(SlotChangeRequest.RequestStatus.APPROVED);
            request.setApprovedBy(admin);
            request.setAdminNotes(reviewRequest.getNotes());

            // Update the actual visit
            if (request.getVisit() != null) {
                updateVisitSlot(request.getVisit(), request);
            } else if (request.getManagerVisit() != null) {
                updateManagerVisitSlot(request.getManagerVisit(), request);
            }

        } else if ("REJECT".equalsIgnoreCase(reviewRequest.getAction())) {
            request.setStatus(SlotChangeRequest.RequestStatus.REJECTED);
            request.setApprovedBy(admin);
            request.setAdminNotes(reviewRequest.getNotes());
        }

        SlotChangeRequest entity = requestRepository.save(request);
        return convertToResponse(entity);
    }

    private void updateVisitSlot(Visit visit, SlotChangeRequest request) {
        LocalDate today = LocalDate.now();
        LocalDate visitDate; // declare here
        if(today.getDayOfMonth() == 2){
            visitDate = calculateVisitDate(
                    request.getRequestedWeekNumber(),
                    request.getRequestedDayOfWeek()
            );
        }else{
            visitDate = calculateVisitDateCurrentMonth(
                    request.getRequestedWeekNumber(),
                    request.getRequestedDayOfWeek()
            );
        }
        visit.setVisitDate(visitDate);
        visit.setWeekNumber(request.getRequestedWeekNumber());
        visit.setDayOfWeek(request.getRequestedDayOfWeek());
        visit.setScheduledDate(visitDate.atStartOfDay());
        visitRepository.save(visit);
    }

    private void updateManagerVisitSlot(ManagerVisit managerVisit, SlotChangeRequest request) {
        LocalDate today = LocalDate.now();
        LocalDate visitDate; // declare here
        if(today.getDayOfMonth() == 2){
            visitDate = calculateVisitDate(
                    request.getRequestedWeekNumber(),
                    request.getRequestedDayOfWeek()
            );
        }else{
            visitDate = calculateVisitDateCurrentMonth(
                    request.getRequestedWeekNumber(),
                    request.getRequestedDayOfWeek()
            );
        }

        managerVisit.setVisitDate(visitDate);
        managerVisit.setWeekNumber(request.getRequestedWeekNumber());
        managerVisit.setDayOfWeek(request.getRequestedDayOfWeek());
        managerVisit.setScheduledDate(visitDate.atStartOfDay());
        managerVisitRepository.save(managerVisit);
    }

    @Transactional
    public List<SlotChangeRequestResponse> getPendingRequests() {
        return requestRepository.findByStatusOrderByRequestedAtDesc(SlotChangeRequest.RequestStatus.PENDING)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<SlotChangeRequestResponse> getUserRequests(Long userId, String userType) {
        if ("MANAGER".equalsIgnoreCase(userType)) {
            return requestRepository.findByRequestedManagerIdOrderByRequestedAtDesc(userId)
                    .stream()
                    .filter(r -> r.getStatus() == SlotChangeRequest.RequestStatus.PENDING)
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else if ("FIELD_EXECUTIVE".equalsIgnoreCase(userType)) {
            // You'll need to create this method if you add requestedFieldExecutive to entity
            // return requestRepository.findByRequestedFieldExecutiveIdOrderByRequestedAtDesc(userId)...
            return requestRepository.findByVisitFieldExecutiveIdAndStatusOrderByRequestedAtDesc(
                            userId, SlotChangeRequest.RequestStatus.PENDING)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Transactional
    public List<SlotChangeRequestResponse> getFieldExecutiveRequests(Long fieldExecutiveId) {
        return requestRepository.findByVisitFieldExecutiveIdOrderByRequestedAtDesc(fieldExecutiveId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    public SlotChangeRequestResponse convertToResponse(SlotChangeRequest request) {
        SlotChangeRequestResponse response = new SlotChangeRequestResponse();
        response.setId(request.getId());

        if (request.getVisit() != null) {
            response.setVisitId(request.getVisit().getId());
            response.setVisitType(request.getVisit().getVisitType().name());
            response.setTargetName(getTargetName(request.getVisit()));
        } else if (request.getManagerVisit() != null) {
            response.setManagerVisitId(request.getManagerVisit().getId());
            response.setVisitType(request.getManagerVisit().getVisitType().name());
            response.setTargetName(request.getManagerVisit().getDoctorName());
        }

        response.setCurrentSchedule(String.format("Week %d, Day %d, Date %s",
                request.getCurrentWeekNumber(),
                request.getCurrentDayOfWeek(),
                request.getCurrentVisitDate()));

        response.setRequestedSchedule(String.format("Week %d, Day %d, Date %s",
                request.getRequestedWeekNumber(),
                request.getRequestedDayOfWeek(),
                request.getRequestedVisitDate()));

        // Set requester info
        if (request.getRequestedManager() != null) {
            response.setRequestedByName(request.getRequestedManager().getName());
            response.setRequestedByRole("MANAGER");
        } else {
            // If you add requestedFieldExecutive field
             response.setRequestedByName(request.getRequestedFieldExecutive().getName());
             response.setRequestedByRole("FIELD_EXECUTIVE");

            // Temporary workaround for field executive
            if (request.getVisit() != null && request.getVisit().getFieldExecutive() != null) {
                response.setRequestedByName(request.getVisit().getFieldExecutive().getName());
                response.setRequestedByRole("FIELD_EXECUTIVE");
            }
        }

        response.setStatus(request.getStatus().name());
        response.setReason(request.getReason());
        response.setAdminNotes(request.getAdminNotes());
        response.setRequestedAt(request.getRequestedAt());
        response.setReviewedAt(request.getReviewedAt());

        if (request.getApprovedBy() != null) {
            response.setApprovedByName(request.getApprovedBy().getName());
        }

        return response;
    }


    private String getTargetName(Visit visit) {
        switch (visit.getVisitType()) {
            case DOCTOR:
                return visit.getDoctor() != null ? visit.getDoctor().getName() : "Doctor";
            case PHARMACIST:
                return visit.getPharmacy() != null ? visit.getPharmacy().getPharmacyName() : "Pharmacy";
            case STOCKIST:
                return visit.getStockist() != null ? visit.getStockist().getName() : "Stockist";
            default:
                return "Unknown";
        }
    }

    @Transactional
    public void cancelRequest(Long requestId, Long userId, String userType) {
        SlotChangeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Check authorization
        boolean isAuthorized = false;
        if ("MANAGER".equalsIgnoreCase(userType)) {
            isAuthorized = request.getRequestedManager() != null &&
                    request.getRequestedManager().getId().equals(userId);
        } else if ("FIELD_EXECUTIVE".equalsIgnoreCase(userType)) {
            // For field executive, check if they own the visit
            isAuthorized = request.getVisit() != null &&
                    request.getVisit().getFieldExecutive().getId().equals(userId);
        }

        if (!isAuthorized) {
            throw new RuntimeException("Not authorized to cancel this request");
        }

        if (request.getStatus() != SlotChangeRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be cancelled");
        }

        request.setStatus(SlotChangeRequest.RequestStatus.CANCELLED);
        requestRepository.save(request);
    }
}