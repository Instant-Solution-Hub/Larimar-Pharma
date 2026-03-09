package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    List<Pharmacy> findByFieldExecutiveId(Long fieldExecutiveId);
}
