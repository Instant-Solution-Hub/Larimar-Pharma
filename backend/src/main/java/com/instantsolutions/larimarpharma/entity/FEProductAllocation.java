package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fe_product_allocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FEProductAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fe_id", nullable = false)
    private FieldExecutive fieldExecutive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer allocatedQuantity;

    @Column(nullable = false)
    private Integer remainingQuantity;
}
