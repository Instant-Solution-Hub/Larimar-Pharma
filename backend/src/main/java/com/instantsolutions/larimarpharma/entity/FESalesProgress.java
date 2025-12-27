package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.YearMonth;

@Entity
@Table(
        name = "fe_sales_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"field_executive_id", "product_id", "sales_month"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FESalesProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = false)
    private FieldExecutive fieldExecutive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Store month as YYYY-MM (recommended)
     */
    @Column(name = "sales_month", nullable = false)
    private YearMonth salesMonth;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double totalSales;

    @PrePersist
    @PreUpdate
    private void calculateTotal() {
        if (price != null && quantity != null) {
            this.totalSales = price * quantity;
        }
    }
}
