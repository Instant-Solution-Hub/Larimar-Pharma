package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "converted_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvertedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;
    private Double value;
}
