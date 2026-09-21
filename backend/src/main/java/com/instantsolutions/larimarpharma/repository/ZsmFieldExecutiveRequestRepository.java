package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.ZsmFieldExecutiveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ZsmFieldExecutiveRequestRepository
        extends JpaRepository<ZsmFieldExecutiveRequest, Long> {

    // Check for existing pending request for same ZSM + week/day
    @Query("""
        SELECT r FROM ZsmFieldExecutiveRequest r
        WHERE r.zsmAdmin.id = :zsmId
          AND r.weekNumber = :weekNumber
          AND r.dayOfWeek = :dayOfWeek
          AND r.status = 'PENDING'
    """)
    List<ZsmFieldExecutiveRequest> findPendingByZsmAndSlot(
            @Param("zsmId") Long zsmId,
            @Param("weekNumber") Integer weekNumber,
            @Param("dayOfWeek") Integer dayOfWeek
    );

    // All requests by a ZSM
    List<ZsmFieldExecutiveRequest> findByZsmAdminIdOrderByCreatedAtDesc(Long zsmId);

    // All pending requests (for admin dashboard)
    List<ZsmFieldExecutiveRequest> findByStatusOrderByCreatedAtAsc(
            ZsmFieldExecutiveRequest.RequestStatus status
    );

    // Prevent duplicate: same ZSM requesting same FE for same slot
    @Query("""
    SELECT COUNT(r) > 0 FROM ZsmFieldExecutiveRequest r
    WHERE r.zsmAdmin.id = :zsmId
      AND r.requestedFieldExecutive.id = :feId
      AND r.targetDate = :targetDate
      AND r.status = 'PENDING'
""")
    boolean existsPendingRequest(
            @Param("zsmId") Long zsmId,
            @Param("feId") Long feId,
            @Param("targetDate") LocalDate targetDate
    );
}