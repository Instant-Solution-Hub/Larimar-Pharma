package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "territory_monthly_targets",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "manager_id", "territory", "month", "year"
                })
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerritoryMonthlyTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    @Column(nullable = false)
    private String territory;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    // ===== TARGETS (AUTO-FILLED INITIALLY) =====
    private Double primaryTargetSet;
    private Double secondaryTargetSet;

    // ===== MANAGER-CONTROLLED VALUES =====
    private Double primaryTargetAchieved;
    private Double secondaryTargetAchieved;

    private Integer subStockistStock;

    // ===== DERIVED (optional to store) =====
    private Double primaryDeficit;
    private Double secondaryDeficit;

    @PrePersist
    @PreUpdate
    void calculateDeficit() {
        this.primaryDeficit =
                (primaryTargetSet == null ? 0 : primaryTargetSet)
                        - (primaryTargetAchieved == null ? 0 : primaryTargetAchieved);

        this.secondaryDeficit =
                (secondaryTargetSet == null ? 0 : secondaryTargetSet)
                        - (secondaryTargetAchieved == null ? 0 : secondaryTargetAchieved);
    }
}

