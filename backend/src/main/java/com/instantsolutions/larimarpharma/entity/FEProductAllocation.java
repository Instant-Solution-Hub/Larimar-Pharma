package com.instantsolutions.larimarpharma.entity;

import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fe_product_allocation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fe_id", "product_id", "month", "year"})
        })
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

    private Integer month;
    private Integer year;

    @Column(nullable = false)
    private Integer allocatedQuantity;

    @Column(nullable = false)
    private Integer remainingQuantity;
}
