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

    List<LeaveRequest> findByFieldExecutiveIdOrderByFromDateDesc(Long fieldExecutiveId);

    List<LeaveRequest> findByManagerIdOrderByFromDateDesc(Long managerId);

    @Query("""
    SELECT l FROM LeaveRequest l
    WHERE l.manager.id = :managerId
      AND l.status = com.instantsolutions.larimarpharma.entity.LeaveRequest.ApprovalStatus.APPROVED
      AND (
           l.fromDate <= :monthEnd
           AND l.toDate >= :monthStart
      )
""")
    List<LeaveRequest> findApprovedManagerLeavesForMonth(
            @Param("managerId") Long managerId,
            @Param("monthStart") LocalDateTime monthStart,
            @Param("monthEnd") LocalDateTime monthEnd
    );



}
