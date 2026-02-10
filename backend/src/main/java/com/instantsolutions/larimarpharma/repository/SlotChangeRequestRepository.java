package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.SlotChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SlotChangeRequestRepository extends JpaRepository<SlotChangeRequest, Long> {

    List<SlotChangeRequest> findByStatusOrderByRequestedAtDesc(SlotChangeRequest.RequestStatus status);

    List<SlotChangeRequest> findByRequestedManagerIdOrderByRequestedAtDesc(Long managerId);

    @Query("SELECT r FROM SlotChangeRequest r WHERE r.visit.fieldExecutive.id = :fieldExecutiveId ORDER BY r.requestedAt DESC")
    List<SlotChangeRequest> findByVisitFieldExecutiveIdOrderByRequestedAtDesc(@Param("fieldExecutiveId") Long fieldExecutiveId);

    @Query("SELECT r FROM SlotChangeRequest r WHERE r.visit.fieldExecutive.id = :fieldExecutiveId AND r.status = :status ORDER BY r.requestedAt DESC")
    List<SlotChangeRequest> findByVisitFieldExecutiveIdAndStatusOrderByRequestedAtDesc(
            @Param("fieldExecutiveId") Long fieldExecutiveId,
            @Param("status") SlotChangeRequest.RequestStatus status);

    List<SlotChangeRequest> findByVisitId(Long visitId);

    List<SlotChangeRequest> findByManagerVisitId(Long managerVisitId);

    @Query("SELECT r FROM SlotChangeRequest r WHERE r.approvedBy.id = :adminId ORDER BY r.reviewedAt DESC")
    List<SlotChangeRequest> findByApprovedByIdOrderByReviewedAtDesc(@Param("adminId") Long adminId);
}