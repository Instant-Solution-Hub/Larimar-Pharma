package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Query("SELECT fe FROM FieldExecutive fe WHERE " +
            "LOWER(fe.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(fe.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(fe.employeeCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<FieldExecutive> search(@Param("searchTerm") String searchTerm);

    boolean existsByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);

    @Query("SELECT COUNT(fe) FROM FieldExecutive fe WHERE fe.active = true")
    long countActiveExecutives();
}
