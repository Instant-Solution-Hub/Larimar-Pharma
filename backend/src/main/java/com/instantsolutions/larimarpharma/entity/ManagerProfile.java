package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "manager_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false, unique = true)
    private Manager manager;

    private Integer teamSize;
    private Double teamTargetAchieved;
    private Double teamComplianceRate;

    // Manager performance metrics
    private Integer totalApprovalsProcessed;
    private Integer pendingApprovals;

    @Builder.Default
    private Integer casualLeaves = 20;

    @Builder.Default
    private Integer sickLeaves = 20;

    @Builder.Default
    private Integer approvedCasualLeaves = 0;

    @Builder.Default
    private Integer approvedSickLeaves = 0;

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
