package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @Query("""
    SELECT lr FROM LeaveRequest lr
    WHERE lr.fieldExecutive.id = :feId
      AND lr.status = com.instantsolutions.larimarpharma.entity.LeaveRequest.ApprovalStatus.APPROVED
      AND lr.fromDate <= :endDate
      AND lr.toDate >= :startDate
""")
    List<LeaveRequest> findApprovedLeavesForMonth(
            @Param("feId") Long feId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
