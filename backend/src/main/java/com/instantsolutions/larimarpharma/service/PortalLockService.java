package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.FieldExecutiveResponse;
import com.instantsolutions.larimarpharma.DTOs.ManagerResponseDto;
import com.instantsolutions.larimarpharma.DTOs.PortalUnlockRequestDto;
import com.instantsolutions.larimarpharma.DTOs.UserIdentityDto;
import com.instantsolutions.larimarpharma.entity.*;
import com.instantsolutions.larimarpharma.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortalLockService {

    private final VisitRepository visitRepository;
    private final ManagerVisitRepository managerVisitRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PortalLockStatusRepository portalLockStatusRepository;
    private final PortalUnlockRequestRepository portalUnlockRequestRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final ManagerRepository managerRepository;
    private final AdminRepository adminRepository;

    /**
     * Daily job to check and lock portals for users who missed visits
     * Runs every day at 11:00 PM
     */
//    @Scheduled(cron = "0 0 23 * * *") // 11 PM daily
//    @Transactional
//    public void checkAndLockPortals() {
//        LocalDate yesterday = LocalDate.now().minusDays(1);
//        log.info("Checking portal locks for date: {}", yesterday);
//
//        // Check field executives
//        checkFieldExecutives(yesterday);
//
//        // Check managers
//        checkManagers(yesterday);
//    }
//
//    private void checkFieldExecutives(LocalDate date) {
//        List<FieldExecutive> fieldExecutives = fieldExecutiveRepository.findAll();
//
//        for (FieldExecutive fe : fieldExecutives) {
//            try {
//                if (shouldLockFieldExecutivePortal(fe, date)) {
//                    lockFieldExecutivePortal(fe, date);
//                }
//            } catch (Exception e) {
//                log.error("Error checking portal lock for FE: {}", fe.getId(), e);
//            }
//        }
//    }
//
//
//    private void checkManagers(LocalDate date) {
//        List<Manager> managers = managerRepository.findAll();
//
//        for (Manager manager : managers) {
//            try {
//                if (shouldLockManagerPortal(manager, date)) {
//                    lockManagerPortal(manager, date);
//                }
//            } catch (Exception e) {
//                log.error("Error checking portal lock for Manager: {}", manager.getId(), e);
//            }
//        }
//    }

    private boolean shouldLockFieldExecutivePortal(FieldExecutive fe, LocalDate date) {
        boolean isAdminUnlocked = portalLockStatusRepository.existsByFieldExecutiveAndIsUnlocked(fe, true);
        if(isAdminUnlocked){
            return false;
        }
        // Check if it's a weekend
        if (isWeekend(date)) {
            return false;
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        // Check if FE was on approved leave
        if (isFieldExecutiveOnLeave(fe, date)) {
            log.debug("FE {} was on approved leave on {}, skipping lock", fe.getId(), date);
            return false;
        }

        // Check if FE marked any visit for that day
        boolean hasCompletedVisits = visitRepository.existsCompletedVisitsByFieldExecutiveAndDate(fe, start, end);

        return !hasCompletedVisits;
    }

    private boolean shouldLockManagerPortal(Manager manager, LocalDate date) {
        boolean isAdminUnlocked = portalLockStatusRepository.existsByManagerAndIsUnlocked(manager, true);

        // Check if the admin unlocked
        if(isAdminUnlocked){
            return  false;
        }
        // Check if it's a weekend
        if (isWeekend(date)) {
            return false;
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();

        // Check if manager was on approved leave
        if (isManagerOnLeave(manager, date)) {
            log.debug("Manager {} was on approved leave on {}, skipping lock", manager.getId(), date);
            return false;
        }

        // Check if manager has scheduled manager visits
        boolean hasScheduledManagerVisits = managerVisitRepository.existsScheduledManagerVisits(manager, start, end);

        // If manager has no scheduled activities, don't lock
        if (!hasScheduledManagerVisits) {
            log.debug("Manager {} has no scheduled activities on {}", manager.getId(), date);
            return false;
        }

        // Check if manager completed any scheduled activities
        boolean completedActivities = hasCompletedManagerActivities(manager, date);

        return !completedActivities;
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private boolean isFieldExecutiveOnLeave(FieldExecutive fe, LocalDate date) {
        return leaveRequestRepository.existsApprovedLeaveForFieldExecutiveOnDate(fe, date);
    }

    private boolean isManagerOnLeave(Manager manager, LocalDate date) {
        return leaveRequestRepository.existsApprovedLeaveForManagerOnDate(manager, date);
    }

    private boolean hasCompletedManagerActivities(Manager manager, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();
        return managerVisitRepository.existsCompletedManagerVisits(manager, start, end);
    }

    private void lockFieldExecutivePortal(FieldExecutive fe, LocalDate date) {
        // Check if already locked for this date
        boolean alreadyLocked = portalLockStatusRepository
                .existsByFieldExecutiveAndLockedDateAndIsLocked(fe, date, true);

        if (alreadyLocked) {
            log.debug("FE {} already locked for date {}", fe.getId(), date);
            return;
        }

        PortalLockStatus lockStatus = PortalLockStatus.builder()
                .fieldExecutive(fe)
                .userType(PortalLockStatus.UserType.FIELD_EXECUTIVE)
                .lockedDate(date)
                .isLocked(true)
                .isUnlocked(false)
                .lockReason("No visits marked for scheduled visit day")
                .unlockRequested(false)
                .build();

        portalLockStatusRepository.save(lockStatus);
        log.info("Locked portal for FE {} on date {}", fe.getId(), date);
    }

    private void lockManagerPortal(Manager manager, LocalDate date) {
        // Check if already locked for this date
        boolean alreadyLocked = portalLockStatusRepository
                .existsByManagerAndLockedDateAndIsLocked(manager, date, true);

        if (alreadyLocked) {
            log.debug("Manager {} already locked for date {}", manager.getId(), date);
            return;
        }

        PortalLockStatus lockStatus = PortalLockStatus.builder()
                .manager(manager)
                .userType(PortalLockStatus.UserType.MANAGER)
                .lockedDate(date)
                .isLocked(true)
                .isUnlocked(false)
                .lockReason("No activities completed for scheduled work day")
                .unlockRequested(false)
                .build();

        portalLockStatusRepository.save(lockStatus);
        log.info("Locked portal for Manager {} on date {}", manager.getId(), date);
    }

    /**
     * Check if user's portal is locked (using DTO)
     */
    public boolean isPortalLocked(UserIdentityDto userIdentifier) {
        Long userId = userIdentifier.getUserId();
        String userType = userIdentifier.getUserType();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        if ("FIELD_EXECUTIVE".equals(userType)) {
            FieldExecutive fe = fieldExecutiveRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Field Executive not found with ID: " + userId));
            if (shouldLockFieldExecutivePortal(fe, yesterday)) {
                lockFieldExecutivePortal(fe, yesterday);
            }
            return portalLockStatusRepository.existsByFieldExecutiveAndIsLocked(fe, true);
        } else if ("MANAGER".equals(userType)) {
            Manager manager = managerRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + userId));
            if(shouldLockManagerPortal(manager, yesterday)){
                lockManagerPortal(manager,yesterday);
            }
            return portalLockStatusRepository.existsByManagerAndIsLocked(manager, true);
        }

        return false;
    }


    /**
     * Generic unlock request method using userId and userType
     */
    @Transactional
    public PortalUnlockRequest requestUnlock(Long userId, String userType, String reason) {
        if ("FIELD_EXECUTIVE".equals(userType)) {
            FieldExecutive fe = fieldExecutiveRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Field Executive not found with ID: " + userId));
            return requestFieldExecutiveUnlock(fe, reason);
        } else if ("MANAGER".equals(userType)) {
            Manager manager = managerRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + userId));
            return requestManagerUnlock(manager, reason);
        } else {
            throw new IllegalArgumentException("Unlock request only supported for FIELD_EXECUTIVE and MANAGER");
        }
    }

    /**
     * Request unlock for locked portal - Field Executive
     */
    @Transactional
    public PortalUnlockRequest requestFieldExecutiveUnlock(FieldExecutive fe, String reason) {

        // Get active lock
        PortalLockStatus lockStatus = portalLockStatusRepository
                .findTopByFieldExecutiveAndIsLockedOrderByLockedDateDesc(fe, true)
                .orElseThrow(() -> new IllegalStateException("Field Executive portal is not locked"));

        // Check if request already exists for same lock date
        boolean alreadyRequested = portalUnlockRequestRepository
                .existsByFieldExecutiveAndLockedDateAndStatusIn(
                        fe,
                        lockStatus.getLockedDate(),   // LocalDate now
                        List.of(PortalUnlockRequest.ApprovalStatus.PENDING)
                );

        if (alreadyRequested) {
            throw new IllegalStateException("Unlock request already submitted for this lock date");
        }

        // Create unlock request
        PortalUnlockRequest unlockRequest = PortalUnlockRequest.builder()
                .fieldExecutive(fe)
                .userType(PortalUnlockRequest.UserType.FIELD_EXECUTIVE)
                .lockedDate(lockStatus.getLockedDate())
                .reason(reason)
                .status(PortalUnlockRequest.ApprovalStatus.PENDING)
                .build();

        // Update lock status
        lockStatus.setUnlockRequested(true);
        lockStatus.setUnlockRequestStatus(PortalLockStatus.UnlockRequestStatus.PENDING);
        lockStatus.setUnlockRequestReason(reason);
        lockStatus.setUnlockRequestDate(LocalDateTime.now());

        portalLockStatusRepository.save(lockStatus);

        return portalUnlockRequestRepository.save(unlockRequest);
    }

    /**
     * Request unlock for locked portal - Manager
     */
    @Transactional
    public PortalUnlockRequest requestManagerUnlock(Manager manager, String reason) {
        // Get active lock
        PortalLockStatus lockStatus = portalLockStatusRepository
                .findTopByManagerAndIsLockedOrderByLockedDateDesc(manager, true)
                .orElseThrow(() -> new IllegalStateException("Manager portal is not locked"));

        // Create unlock request
        PortalUnlockRequest unlockRequest = PortalUnlockRequest.builder()
                .manager(manager)
                .userType(PortalUnlockRequest.UserType.MANAGER)
                .lockedDate(lockStatus.getLockedDate())
                .reason(reason)
                .status(PortalUnlockRequest.ApprovalStatus.PENDING)
                .build();

        // Update lock status
        lockStatus.setUnlockRequested(true);
        lockStatus.setUnlockRequestStatus(PortalLockStatus.UnlockRequestStatus.PENDING);
        lockStatus.setUnlockRequestReason(reason);
        lockStatus.setUnlockRequestDate(LocalDateTime.now());

        portalLockStatusRepository.save(lockStatus);
        return portalUnlockRequestRepository.save(unlockRequest);
    }

    /**
     * Get user unlock requests
     */
//    public List<PortalUnlockRequest> getUserUnlockRequests(Long userId, String userType) {
//        if ("FIELD_EXECUTIVE".equals(userType)) {
//            FieldExecutive fe = fieldExecutiveRepository.findById(userId)
//                    .orElseThrow(() -> new RuntimeException("Field Executive not found with ID: " + userId));
//            return portalUnlockRequestRepository.findByFieldExecutiveOrderByCreatedAtDesc(fe);
//        } else if ("MANAGER".equals(userType)) {
//            Manager manager = managerRepository.findById(userId)
//                    .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + userId));
//            return portalUnlockRequestRepository.findByManagerOrderByCreatedAtDesc(manager);
//        }
//        throw new IllegalArgumentException("Invalid user type: " + userType);
//    }

    /**
     * Admin approves/rejects unlock request
     */
    @Transactional
    public void processUnlockRequest(Long requestId, boolean approve, Long adminId, String comments) {
        PortalUnlockRequest request = portalUnlockRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Unlock request not found"));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

        PortalLockStatus lockStatus;

        if (request.getUserType() == PortalUnlockRequest.UserType.FIELD_EXECUTIVE) {
            lockStatus = portalLockStatusRepository
                    .findTopByFieldExecutiveAndLockedDateOrderByIdDesc(
                            request.getFieldExecutive(),
                            request.getLockedDate())
                    .orElseThrow(() -> new RuntimeException("Lock status not found"));
        } else {
            lockStatus = portalLockStatusRepository
                    .findTopByManagerAndLockedDateOrderByIdDesc(
                            request.getManager(),
                            request.getLockedDate())
                    .orElseThrow(() -> new RuntimeException("Lock status not found"));
        }

        if (approve) {
            // Approve unlock
            lockStatus.setIsLocked(false);
            lockStatus.setIsUnlocked(true);
            lockStatus.setUnlockRequestStatus(PortalLockStatus.UnlockRequestStatus.APPROVED);
            lockStatus.setApprovedBy(admin);
            lockStatus.setUnlockApprovalDate(LocalDateTime.now());

            request.setStatus(PortalUnlockRequest.ApprovalStatus.APPROVED);
            request.setReviewedBy(admin);
            request.setAdminComments(comments);
            request.setReviewedAt(LocalDateTime.now());
        } else {
            // Reject unlock
            lockStatus.setUnlockRequested(false);
            lockStatus.setIsUnlocked(false);
            lockStatus.setUnlockRequestStatus(PortalLockStatus.UnlockRequestStatus.REJECTED);

            request.setStatus(PortalUnlockRequest.ApprovalStatus.REJECTED);
            request.setReviewedBy(admin);
            request.setAdminComments(comments);
            request.setReviewedAt(LocalDateTime.now());
        }

        portalLockStatusRepository.save(lockStatus);
        portalUnlockRequestRepository.save(request);
    }

    /**
     * Manual lock portal (admin function)
     */
    @Transactional
    public void manuallyLockPortal(Long userId, String userType, LocalDate date, String reason, Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

        if ("FIELD_EXECUTIVE".equals(userType)) {
            FieldExecutive fe = fieldExecutiveRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Field Executive not found with ID: " + userId));
            manuallyLockFieldExecutivePortal(fe, date, reason, admin);
        } else if ("MANAGER".equals(userType)) {
            Manager manager = managerRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + userId));
            manuallyLockManagerPortal(manager, date, reason, admin);
        } else {
            throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

    @Transactional
    public void manuallyLockFieldExecutivePortal(FieldExecutive fe, LocalDate date, String reason, Admin admin) {
        lockFieldExecutivePortal(fe, date);

        // Update with admin info if needed
        PortalLockStatus lockStatus = portalLockStatusRepository
                .findTopByFieldExecutiveAndLockedDateOrderByIdDesc(fe, date)
                .orElseThrow(() -> new RuntimeException("Lock not found after creation"));

        lockStatus.setLockReason(reason + " (Manually locked by Admin: " + admin.getEmail() + ")");
        portalLockStatusRepository.save(lockStatus);
    }

    @Transactional
    public void manuallyLockManagerPortal(Manager manager, LocalDate date, String reason, Admin admin) {
        lockManagerPortal(manager, date);

        PortalLockStatus lockStatus = portalLockStatusRepository
                .findTopByManagerAndLockedDateOrderByIdDesc(manager, date)
                .orElseThrow(() -> new RuntimeException("Lock not found after creation"));

        lockStatus.setLockReason(reason + " (Manually locked by Admin: " + admin.getEmail() + ")");
        portalLockStatusRepository.save(lockStatus);
    }

    /**
     * Get all active locks for a field executive
     */
    public List<PortalLockStatus> getFieldExecutiveActiveLocks(Long feId) {
        FieldExecutive fe = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new RuntimeException("Field Executive not found with ID: " + feId));
        return portalLockStatusRepository
                .findByFieldExecutiveAndIsLockedOrderByLockedDateDesc(fe, true);
    }

    /**
     * Get all active locks for a manager
     */
    public List<PortalLockStatus> getManagerActiveLocks(Long managerId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + managerId));
        return portalLockStatusRepository
                .findByManagerAndIsLockedOrderByLockedDateDesc(manager, true);
    }

    /**
     * Get all unlock requests
     */
    @Transactional
    public List<PortalUnlockRequestDto> getAllUnlockRequests() {
        return portalUnlockRequestRepository
                .findAll().stream().map(this::mapToPortalUnlockDto).toList();
    }

    public PortalUnlockRequestDto mapToPortalUnlockDto(PortalUnlockRequest request) {

        ManagerResponseDto managerDto = null;
        FieldExecutiveResponse feDto = null;

        if (request.getManager() != null) {
            Manager m = request.getManager();
            managerDto = ManagerResponseDto.builder()
                    .id(m.getId())
                    .name(m.getName())
                    .email(m.getEmail())
                    .phone(m.getPhone())
                    .employeeCode(m.getEmployeeCode())
                    .build();
        }

        if (request.getFieldExecutive() != null) {
            FieldExecutive fe = request.getFieldExecutive();
            feDto = FieldExecutiveResponse.builder()
                    .id(fe.getId())
                    .name(fe.getName())
                    .email(fe.getEmail())
                    .phone(fe.getPhone())
                    .employeeCode(fe.getEmployeeCode())
                    .build();
        }

        return PortalUnlockRequestDto.builder()
                .id(request.getId())
                .userType(request.getUserType())
                .lockedDate(request.getLockedDate())
                .reason(request.getReason())
                .status(request.getStatus())
                .requestedAt(request.getRequestedAt())
                .reviewedAt(request.getReviewedAt())
                .adminComments(request.getAdminComments())
                .manager(managerDto)
                .fieldExecutive(feDto)
                .build();
    }



}