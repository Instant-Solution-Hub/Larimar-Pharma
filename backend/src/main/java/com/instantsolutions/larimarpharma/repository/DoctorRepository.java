package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByActiveTrue();

    List<Doctor> findByActiveFalse();
}
