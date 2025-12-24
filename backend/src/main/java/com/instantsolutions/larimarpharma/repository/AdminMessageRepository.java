package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.AdminMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminMessageRepository extends JpaRepository<AdminMessage, Long> {

    List<AdminMessage> findAllByFieldExecutiveIdOrderByCreatedAtDesc(Long feId);

    List<AdminMessage> findAllBySuperAdminIdOrderByCreatedAtDesc(Long adminId);
}
