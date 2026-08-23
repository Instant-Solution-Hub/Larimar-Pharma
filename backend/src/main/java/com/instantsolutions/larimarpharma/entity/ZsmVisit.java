package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "zsm_visits",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"zsm_id", "original_visit_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZsmVisit {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===== OWNERS ===== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zsm_id", nullable = false)
    private Admin zsmAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = true)
    private FieldExecutive fieldExecutive;

    /* ===== LINK TO ORIGINAL VISIT ===== */

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
            name = "original_visit_id",
            unique = true,
            nullable = true
    )
    private Visit originalVisit;

    @OneToMany(
            mappedBy = "zsmVisit",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<SlotChangeRequest> slotChangeRequests = new ArrayList<>();



    @Column(nullable = false)
    @Builder.Default
    private boolean unscheduled = false;

    /* ===== REPLICATED VISIT DATA ===== */

    @ElementCollection
    @CollectionTable(name = "zsm_visit_activities", joinColumns = @JoinColumn(name = "visit_id"))
    @Column(name = "activity")
    @Builder.Default
    private List<String> activitiesPerformed = List.of();

    private LocalDate visitDate;
    private Integer weekNumber;
    private Integer dayOfWeek;
    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;
    @Enumerated(EnumType.STRING)
    private Visit.VisitType visitType;

    @Enumerated(EnumType.STRING)
    private Visit.VisitStatus status;

    /* Doctor snapshot */
    private Long doctorId;
    private String doctorName;
    private String doctorDesignation;

    @Enumerated(EnumType.STRING)
    private Doctor.Category doctorCategory;

    private String hospitalName;

    // Product relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    /* ===== MANAGER-SPECIFIC DATA ===== */

    private LocalDateTime joinedAt;
//    private LocalDateTime assignedAt;

    private String managerNotes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        joinedAt = LocalDateTime.now();
    }
}
