package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Manager;
import com.instantsolutions.larimarpharma.entity.SlotPlanningDayRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SlotPlanningDayRequestRepository extends JpaRepository<SlotPlanningDayRequest, Long> {
    List<SlotPlanningDayRequest> findByRequestedManager(Manager manager);

    List<SlotPlanningDayRequest> findByRequestedFieldExecutive(FieldExecutive fieldExecutive);

    List<SlotPlanningDayRequest> findByStatus(SlotPlanningDayRequest.RequestStatus status);

    @Query("SELECT r FROM SlotPlanningDayRequest r WHERE " +
            "r.requestedManager = :manager OR r.requestedFieldExecutive = :fe " +
            "ORDER BY r.requestedAt DESC")
    List<SlotPlanningDayRequest> findByManagerOrFieldExecutive(
            @Param("manager") Manager manager,
            @Param("fe") FieldExecutive fe
    );

    @Query("SELECT r FROM SlotPlanningDayRequest r WHERE r.status = :status " +
            "AND r.requestedAt BETWEEN :startDate AND :endDate")
    List<SlotPlanningDayRequest> findByStatusAndDateRange(
            @Param("status") SlotPlanningDayRequest.RequestStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByRequestedManagerAndStatusAndRequestedAt(
            Manager manager,
            SlotPlanningDayRequest.RequestStatus status,
            LocalDate requestedAt
    );

    boolean existsByRequestedFieldExecutiveAndStatusAndRequestedAt(
            FieldExecutive fe,
            SlotPlanningDayRequest.RequestStatus status,
            LocalDate requestedAt
    );

    Optional<SlotPlanningDayRequest> findByRequestedManagerAndStatusAndRequestedAt(
            Manager manager,
            SlotPlanningDayRequest.RequestStatus status,
            LocalDate requestedAt
    );

    Optional<SlotPlanningDayRequest> findByRequestedFieldExecutiveAndStatusAndRequestedAt(
            FieldExecutive fe,
            SlotPlanningDayRequest.RequestStatus status,
            LocalDate requestedAt
    );

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM SlotPlanningDayRequest r " +
            "WHERE r.requestedManager = :manager AND r.status = :status AND r.requestedAt = :date")
    boolean hasApprovedRequestForManager(
            @Param("manager") Manager manager,
            @Param("status") SlotPlanningDayRequest.RequestStatus status,
            @Param("date") LocalDate date
    );

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM SlotPlanningDayRequest r " +
            "WHERE r.requestedFieldExecutive = :fe AND r.status = :status AND r.requestedAt = :date")
    boolean hasApprovedRequestForFieldExecutive(
            @Param("fe") FieldExecutive fe,
            @Param("status") SlotPlanningDayRequest.RequestStatus status,
            @Param("date") LocalDate date
    );
}
