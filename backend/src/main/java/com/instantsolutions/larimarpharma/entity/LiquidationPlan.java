package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "liquidation_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquidationPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = false)
    private FieldExecutive fieldExecutive;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private String medicalShopName;


    @Column(nullable = false)
    private Integer targetLiquidation;

    @Column(nullable = false)
    private LocalDateTime deadline;

    private String strategy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApprovalStatus managerApprovalStatus = ApprovalStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PlanStatus status = PlanStatus.ACTIVE;

    private Integer achievedUnits;

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

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum PlanStatus {
        ACTIVE, PENDING_APPROVAL, COMPLETED, CANCELLED
    }
}
