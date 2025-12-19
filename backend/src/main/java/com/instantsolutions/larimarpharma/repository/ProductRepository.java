package com.instantsolutions.larimarpharma.repository;

import com.instantsolutions.larimarpharma.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    boolean existsByName(String name);

    Optional<Product> findByIdAndActiveTrue(Long id);
}
