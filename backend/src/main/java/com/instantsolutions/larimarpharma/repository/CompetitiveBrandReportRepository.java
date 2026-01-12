package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.CompetitiveBrandReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitiveBrandReportRepository
        extends JpaRepository<CompetitiveBrandReport, Long> {


    List<CompetitiveBrandReport> findByFieldExecutiveIdOrderByCreatedAtDesc(Long fieldExecutiveId);
}
