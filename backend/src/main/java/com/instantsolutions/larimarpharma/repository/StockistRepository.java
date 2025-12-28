package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.Stockist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockistRepository extends JpaRepository<Stockist, Long> {

    /**
     * Get all stockists assigned to a Field Executive
     */
    @Query("""
        SELECT DISTINCT s
        FROM Stockist s
        JOIN s.fieldExecutives fe
        WHERE fe.id = :feId
    """)
    List<Stockist> findAllByFieldExecutiveId(@Param("feId") Long feId);


    /**
     * Authorization check:
     * Verify that a stockist is mapped to a given FE
     */
    @Query("""
        SELECT s
        FROM Stockist s
        JOIN s.fieldExecutives fe
        WHERE s.id = :stockistId
          AND fe.id = :feId
    """)
    Optional<Stockist> findByIdAndFieldExecutive(
            @Param("stockistId") Long stockistId,
            @Param("feId") Long feId
    );


    /**
     * Search stockists by name (case-insensitive)
     */
    List<Stockist> findByNameContainingIgnoreCase(String name);
}
