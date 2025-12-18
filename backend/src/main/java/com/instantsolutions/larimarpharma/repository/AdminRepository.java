package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByEmployeeCode(String employeeCode);
    List<Admin> findByDepartment(String department);
    List<Admin> findByActiveTrue();

    @Query("SELECT a FROM Admin a WHERE " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.department) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Admin> search(@Param("searchTerm") String searchTerm);

    @Query("SELECT a FROM Admin a WHERE a.adminLevel = :level")
    List<Admin> findByAdminLevel(@Param("level") String level);

    boolean existsByEmail(String email);
    boolean existsByEmployeeCode(String employeeCode);

    @Query("SELECT COUNT(a) FROM Admin a WHERE a.active = true")
    long countActiveAdmins();
}