package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.ManagerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerProfileRepository
        extends JpaRepository<ManagerProfile, Long> {

    Optional<ManagerProfile> findByManagerId(Long managerId);
}

