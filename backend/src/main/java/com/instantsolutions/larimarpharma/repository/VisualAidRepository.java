package com.instantsolutions.larimarpharma.repository;


import com.instantsolutions.larimarpharma.entity.VisualAid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisualAidRepository extends JpaRepository<VisualAid, Long> {
    List<VisualAid> findByCategoryIgnoreCase(String category);
}
