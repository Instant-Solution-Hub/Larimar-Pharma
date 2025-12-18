package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
    Optional<SuperAdmin> findByEmail(String email);
    List<SuperAdmin> findByActiveTrue();
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(sa) FROM SuperAdmin sa WHERE sa.active = true")
    long countActiveSuperAdmins();
}