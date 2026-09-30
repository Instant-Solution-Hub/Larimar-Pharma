package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "stockist_product_stock",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_fe_stockist_product_month",
                        columnNames = {
                                "field_executive_id",
                                "stockist_id",
                                "product_id",
                                "stock_month"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockistProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * FE who owns/maintains this stock record
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "field_executive_id", nullable = false)
    private FieldExecutive fieldExecutive;

    /*
     * Stockist selected by the FE
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stockist_id", nullable = false)
    private Stockist stockist;

    /*
     * Product for which stock is being recorded
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /*
     * Always store first day of month.
     *
     * Example:
     * 2026-09-01 = September 2026
     */
    @Column(name = "stock_month", nullable = false)
    private LocalDate stockMonth;

    @Column(nullable = false)
    private Integer quantity;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}