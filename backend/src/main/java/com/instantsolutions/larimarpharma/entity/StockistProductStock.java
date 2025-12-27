package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "stockist_product_stock",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"stockist_id", "product_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockistProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stockist_id", nullable = false)
    private Stockist stockist;


    @Column(nullable = false)
    private Integer availableQuantity;

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
