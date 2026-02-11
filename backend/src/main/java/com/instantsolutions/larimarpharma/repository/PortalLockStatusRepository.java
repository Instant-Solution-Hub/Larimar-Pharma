package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PortalLockStatusRepository extends JpaRepository<PortalLockStatus, Long> {

    // Field Executive methods
    boolean existsByFieldExecutiveAndLockedDateAndIsLocked(
            FieldExecutive fieldExecutive,
            LocalDate date,
            Boolean isLocked);

    boolean existsByFieldExecutiveAndIsLocked(FieldExecutive fieldExecutive, Boolean isLocked);
    boolean existsByFieldExecutiveAndIsUnlocked(FieldExecutive fieldExecutive, Boolean isUnlocked);


    Optional<PortalLockStatus> findTopByFieldExecutiveAndIsLockedOrderByLockedDateDesc(
            FieldExecutive fieldExecutive, Boolean isLocked);

    Optional<PortalLockStatus> findTopByFieldExecutiveAndLockedDateOrderByIdDesc(
            FieldExecutive fieldExecutive, LocalDate lockedDate);

    List<PortalLockStatus> findByFieldExecutiveAndIsLockedOrderByLockedDateDesc(
            FieldExecutive fieldExecutive, Boolean isLocked);

    // Manager methods
    boolean existsByManagerAndLockedDateAndIsLocked(
            Manager manager,
            LocalDate date,
            Boolean isLocked);

    boolean existsByManagerAndIsLocked(Manager manager, Boolean isLocked);
    boolean existsByManagerAndIsUnlocked(Manager manager, Boolean isUnocked);


    Optional<PortalLockStatus> findTopByManagerAndIsLockedOrderByLockedDateDesc(
            Manager manager, Boolean isLocked);

    Optional<PortalLockStatus> findTopByManagerAndLockedDateOrderByIdDesc(
            Manager manager, LocalDate lockedDate);

    List<PortalLockStatus> findByManagerAndIsLockedOrderByLockedDateDesc(
            Manager manager, Boolean isLocked);

    // Find by user type and status
    @Query("SELECT p FROM PortalLockStatus p WHERE " +
            "(p.userType = 'FIELD_EXECUTIVE' AND p.fieldExecutive IS NOT NULL AND p.isLocked = true) OR " +
            "(p.userType = 'MANAGER' AND p.manager IS NOT NULL AND p.isLocked = true)")
    List<PortalLockStatus> findAllActiveLocks();

    // Find pending unlock requests in lock status
    @Query("SELECT p FROM PortalLockStatus p WHERE " +
            "p.unlockRequested = true AND " +
            "p.unlockRequestStatus = 'PENDING'")
    List<PortalLockStatus> findAllPendingUnlockRequests();
}

