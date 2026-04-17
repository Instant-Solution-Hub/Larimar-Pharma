package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Doctor;
import com.instantsolutions.larimarpharma.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByEmail(String email);
    Optional<Manager> findByEmployeeCode(String employeeCode);
    List<Manager> findByDepartment(String department);
    List<Manager> findByActiveTrue();

    @Query("SELECT m FROM Manager m WHERE " +
            "LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(m.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(m.department) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Manager> search(@Param("searchTerm") String searchTerm);

    @Query("SELECT m FROM Manager m JOIN m.managedTerritories t WHERE t = :territory")
    List<Manager> findByManagedTerritory(@Param("territory") String territory);

    boolean existsByEmail(String email);
    boolean existsByEmployeeCode(String employeeCode);

    @Query("SELECT COUNT(m) FROM Manager m WHERE m.active = true")
    long countActiveManagers();

    @Query("""
        SELECT DISTINCT m
        FROM Manager m
        LEFT JOIN FETCH m.fieldExecutives
        LEFT JOIN FETCH m.managedTerritories
        """)
    List<Manager> findAllWithExecutives();

    @Query("""
       SELECT DISTINCT m
       FROM Manager m
       LEFT JOIN FETCH m.fieldExecutives
       LEFT JOIN FETCH m.managedTerritories
       WHERE m.id = :id
       """)
    Optional<Manager> findByIdWithDetails(@Param("id") Long id);

    @Query("""
    SELECT DISTINCT d
    FROM Doctor d
    JOIN d.fieldExecutive fe
    WHERE fe.manager.id = :managerId
""")
    List<Doctor> findDoctorsByManagerId(@Param("managerId") Long managerId);


}