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
  JOIN FETCH mj.doctor d
  WHERE mj.fieldExecutive.id = :feId
    AND MONTH(mj.scheduledTime) = :month
    AND YEAR(mj.scheduledTime) = :year
""")
    List<ManagerJoining> findByFeAndMonth(
            @Param("feId") Long feId,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("""
        SELECT mj
        FROM ManagerJoining mj
        WHERE mj.manager.id = :managerId
          AND MONTH(mj.actualJoiningTime) = :month
          AND YEAR(mj.actualJoiningTime) = :year
        ORDER BY mj.actualJoiningTime DESC
    """)
    List<ManagerJoining> findCurrentMonthByManagerId(
            @Param("managerId") Long managerId,
            @Param("month") int month,
            @Param("year") int year
    );
}
