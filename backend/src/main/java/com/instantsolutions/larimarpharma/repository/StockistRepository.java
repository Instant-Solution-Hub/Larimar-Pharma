package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.DTOs.ManagerStockistResponseDto;
import com.instantsolutions.larimarpharma.DTOs.MonthlyStockistSalesSummaryDto;
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
     * Authorization check:
     * Verify that a stockist is mapped to a given FE
     */



    /**
     * Search stockists by name (case-insensitive)
     */
    List<Stockist> findByNameContainingIgnoreCase(String name);

    List<Stockist> findByActiveTrue();


    @Query("""
        select new com.instantsolutions.larimarpharma.DTOs.ManagerStockistResponseDto(
            s.id,
            s.name,
            s.location
        )
        from Stockist s
        join s.managers m
        where m.id = :managerId
        and s.active = true
    """)
    List<ManagerStockistResponseDto> findStockistsByManagerId(
            @Param("managerId") Long managerId
    );
}
