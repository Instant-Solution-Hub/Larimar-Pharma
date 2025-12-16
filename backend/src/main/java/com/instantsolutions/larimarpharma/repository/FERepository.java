package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FERepository extends JpaRepository<FieldExecutive, Long> {

    Optional<FieldExecutive> findByEmail(String email);

    Optional<FieldExecutive> findByPhoneNumber(String phoneNumber);
}
