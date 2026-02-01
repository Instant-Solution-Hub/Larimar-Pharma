package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "manager_visits",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"manager_id", "original_visit_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===== OWNERS ===== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = false)
    private FieldExecutive fieldExecutive;

    /* ===== LINK TO ORIGINAL VISIT ===== */

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_visit_id", nullable = false, unique = true)
    private Visit originalVisit;

    /* ===== REPLICATED VISIT DATA ===== */

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

    @Enumerated(EnumType.STRING)
    private Doctor.Category doctorCategory;

    private String hospitalName;

    /* ===== MANAGER-SPECIFIC DATA ===== */

    private LocalDateTime joinedAt;
    private String managerNotes;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        joinedAt = LocalDateTime.now();
    }
}
