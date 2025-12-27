package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.Stockist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockistRepository extends JpaRepository<Stockist, Long> {

    // Get all stockists assigned to a Field Executive
    List<Stockist> findAllByFieldExecutiveId(Long fieldExecutiveId);

    // Get a specific stockist by ID and FE (for authorization check)
    Optional<Stockist> findByIdAndFieldExecutiveId(Long stockistId, Long fieldExecutiveId);

    // Optional: Search by name (useful for UI dropdowns)
    List<Stockist> findByNameContainingIgnoreCase(String name);
}
