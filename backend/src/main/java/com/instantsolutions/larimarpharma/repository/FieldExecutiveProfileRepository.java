package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.FieldExecutiveProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FieldExecutiveProfileRepository
        extends JpaRepository<FieldExecutiveProfile, Long> {

    Optional<FieldExecutiveProfile> findByFieldExecutiveId(Long fieldExecutiveId);
}
