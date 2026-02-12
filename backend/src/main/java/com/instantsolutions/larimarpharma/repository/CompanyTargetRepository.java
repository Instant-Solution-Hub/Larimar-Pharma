package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.CompanyTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyTargetRepository extends JpaRepository<CompanyTarget, Long> {

    Optional<CompanyTarget> findByYearAndMonth(Integer year, Integer month);

}

