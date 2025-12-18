package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.CompanyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyPolicyRepository extends JpaRepository<CompanyPolicy, Long> {

    Optional<CompanyPolicy> findByPolicyCode(String policyCode);
    boolean existsByPolicyCode(String policyCode);
}

