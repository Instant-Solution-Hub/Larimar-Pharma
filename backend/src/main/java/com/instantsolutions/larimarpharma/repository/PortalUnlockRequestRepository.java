package com.instantsolutions.larimarpharma.repository;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.PortalUnlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PortalUnlockRequestRepository extends JpaRepository<PortalUnlockRequest, Long> {

    // Field Executive methods
    List<PortalUnlockRequest> findByFieldExecutiveAndStatus(
            FieldExecutive fieldExecutive,
            PortalUnlockRequest.ApprovalStatus status);

    // Manager methods
    List<PortalUnlockRequest> findByManagerAndStatus(
            Manager manager,
            PortalUnlockRequest.ApprovalStatus status);

    // Find all pending requests
    @Query("SELECT r FROM PortalUnlockRequest r WHERE " +
            "r.status = 'PENDING' " +
            "ORDER BY r.requestedAt DESC")
    List<PortalUnlockRequest> findAllPendingRequests();

    // Find by user type
    @Query("SELECT r FROM PortalUnlockRequest r WHERE " +
            "r.userType = :userType AND " +
            "r.status = 'PENDING' " +
            "ORDER BY r.requestedAt DESC")
    List<PortalUnlockRequest> findPendingRequestsByUserType(
            @Param("userType") PortalUnlockRequest.UserType userType);

    // Find by field executive with pending status
    Optional<PortalUnlockRequest> findByFieldExecutiveAndLockedDateAndStatus(
            FieldExecutive fieldExecutive,
            LocalDate lockedDate,
            PortalUnlockRequest.ApprovalStatus status);

    // Find by manager with pending status
    Optional<PortalUnlockRequest> findByManagerAndLockedDateAndStatus(
            Manager manager,
            LocalDate lockedDate,
            PortalUnlockRequest.ApprovalStatus status);

    boolean existsByFieldExecutiveAndLockedDateAndStatusIn(
            FieldExecutive fe,
            LocalDate lockedDate,
            List<PortalUnlockRequest.ApprovalStatus> statuses
    );
}