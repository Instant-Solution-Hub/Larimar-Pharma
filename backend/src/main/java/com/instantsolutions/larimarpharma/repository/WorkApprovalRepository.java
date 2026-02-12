package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.ApprovalRequest;
import com.instantsolutions.larimarpharma.entity.WorkApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkApprovalRepository extends JpaRepository<WorkApproval, Long> {

    List<WorkApproval> findByStatus(ApprovalRequest.ApprovalStatus status);

    List<WorkApproval> findByFieldExecutive_Id(Long feId);

    List<WorkApproval> findByManager_Id(Long managerId);

    List<WorkApproval> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);

    List<WorkApproval> findByFieldExecutiveIdAndWorkDateBetween(
            Long fieldExecutiveId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<WorkApproval> findByManagerIdAndWorkDateBetween(
            Long managerId,
            LocalDate startDate,
            LocalDate endDate
    );
}

