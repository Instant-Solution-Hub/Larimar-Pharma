package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.ManagerJoining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ManagerJoiningRepository extends JpaRepository<ManagerJoining, Long> {

    List<ManagerJoining> findByFieldExecutiveId(Long feId);

    List<ManagerJoining> findByManagerId(Long managerId);

    List<ManagerJoining> findByDoctorId(Long doctorId);

    @Query("""
        SELECT mj
        FROM ManagerJoining mj
        WHERE mj.fieldExecutive.id = :feId
          AND YEAR(mj.scheduledTime) = :year
          AND MONTH(mj.scheduledTime) = :month
    """)
    List<ManagerJoining> findByFeAndMonth(
            @Param("feId") Long feId,
            @Param("month") int month,
            @Param("year") int year
    );
}
