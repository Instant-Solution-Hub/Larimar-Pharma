package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.ManagerFieldExecutiveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ManagerFieldExecutiveRequestRepository
        extends JpaRepository<ManagerFieldExecutiveRequest, Long> {

    @Query("""
        SELECT COUNT(r) > 0 FROM ManagerFieldExecutiveRequest r
        WHERE r.manager.id = :managerId
          AND r.requestedFieldExecutive.id = :feId
          AND r.targetDate = :targetDate
          AND r.status = 'PENDING'
    """)
    boolean existsPendingRequest(
            @Param("managerId") Long managerId,
            @Param("feId") Long feId,
            @Param("targetDate") LocalDate targetDate
    );

    List<ManagerFieldExecutiveRequest> findByManagerIdOrderByCreatedAtDesc(Long managerId);

    List<ManagerFieldExecutiveRequest> findByStatusOrderByCreatedAtAsc(
            ManagerFieldExecutiveRequest.RequestStatus status
    );
}