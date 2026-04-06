package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FieldExecutiveRepository extends JpaRepository<FieldExecutive, Long> {
    Optional<FieldExecutive> findByEmail(String email);
    Optional<FieldExecutive> findByEmployeeCode(String employeeCode);
    List<FieldExecutive> findByManagerId(Long managerId);
    List<FieldExecutive> findByTerritory(String territory);
    List<FieldExecutive> findByRegion(String region);
    List<FieldExecutive> findByActiveTrue();
    @Query("SELECT COUNT(f) FROM FieldExecutive f WHERE f.manager.id = :managerId")
    int countByManagerId(@Param("managerId") Long managerId);

    @Query("SELECT fe FROM FieldExecutive fe WHERE " +
            "LOWER(fe.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(fe.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(fe.employeeCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<FieldExecutive> search(@Param("searchTerm") String searchTerm);


    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmployeeCode(String employeeCode);

    @Query("SELECT COUNT(fe) FROM FieldExecutive fe WHERE fe.active = true")
    long countActiveExecutives();

    @Query("""
    SELECT DISTINCT fe
    FROM Visit v
    JOIN v.fieldExecutive fe
    JOIN v.doctor d
    WHERE fe.manager.id = :managerId
      AND v.visitDate = :visitDate
      AND v.status = com.instantsolutions.larimarpharma.entity.Visit.VisitStatus.SCHEDULED
      AND v.visitType = com.instantsolutions.larimarpharma.entity.Visit.VisitType.DOCTOR
      AND d.category IN (
          com.instantsolutions.larimarpharma.entity.Doctor.Category.A_PLUS,
          com.instantsolutions.larimarpharma.entity.Doctor.Category.A
      )
""")
    List<FieldExecutive> findFEsWithScheduledAPriorityDoctorVisits(
            @Param("managerId") Long managerId,
            @Param("visitDate") LocalDate visitDate
    );


}
