package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.FEProductAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FEProductAllocationRepository
        extends JpaRepository<FEProductAllocation, Long> {

    Optional<FEProductAllocation> findByFieldExecutiveIdAndProductId(
            Long feId,
            Long productId
    );

    List<FEProductAllocation> findByFieldExecutiveId(Long feId);




}
