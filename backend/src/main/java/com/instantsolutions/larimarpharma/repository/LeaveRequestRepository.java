package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.LeaveRequest;
import com.instantsolutions.larimarpharma.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    List<LeaveRequest> findByAdminIdOrderByFromDateDesc(Long managerId);
    @Query("""
    SELECT lr
    FROM LeaveRequest lr
    WHERE lr.fieldExecutive IS NOT NULL
    ORDER BY lr.appliedDate DESC
""")
    List<LeaveRequest> findAllFELeaves();
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

    @Query("""
    SELECT l FROM LeaveRequest l
    WHERE l.admin.id = :adminId
      AND l.status = com.instantsolutions.larimarpharma.entity.LeaveRequest.ApprovalStatus.APPROVED
      AND (
           l.fromDate <= :monthEnd
           AND l.toDate >= :monthStart
      )
""")
    List<LeaveRequest>
    findApprovedAdminLeavesForMonth(
            @Param("adminId") Long adminId,
            @Param("monthStart") LocalDateTime monthStart,
            @Param("monthEnd") LocalDateTime monthEnd
    );


    List<LeaveRequest>
    findByFieldExecutive_Manager_IdOrderByFromDateDesc(Long managerId);

    @Query("""
    SELECT lr
    FROM LeaveRequest lr
    WHERE lr.manager IS NOT NULL
    ORDER BY lr.appliedDate DESC
""")
    List<LeaveRequest> findAllManagerLeaves();

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
            "FROM LeaveRequest l " +
            "WHERE l.fieldExecutive = :fe " +
            "AND l.status = 'APPROVED' " +
            "AND :date BETWEEN DATE(l.fromDate) AND DATE(l.toDate)")
    boolean existsApprovedLeaveForFieldExecutiveOnDate(
            @Param("fe") FieldExecutive fe,
            @Param("date") LocalDate date);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
            "FROM LeaveRequest l " +
            "WHERE l.manager = :manager " +
            "AND l.status = 'APPROVED' " +
            "AND :date BETWEEN DATE(l.fromDate) AND DATE(l.toDate)")
    boolean existsApprovedLeaveForManagerOnDate(
            @Param("manager") Manager manager,
            @Param("date") LocalDate date);




}
