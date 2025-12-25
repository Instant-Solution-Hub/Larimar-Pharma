package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
}
