package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "monthly_target_plans",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"field_executive_id", "month", "year"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTargetPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FE who created the plan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = false)
    private FieldExecutive fieldExecutive;

    @Column(nullable = false)
    private Integer month; // 1 = Jan, 12 = Dec

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Double primarySalesTarget;

    @Column(nullable = false)
    private Double secondarySalesTarget;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
