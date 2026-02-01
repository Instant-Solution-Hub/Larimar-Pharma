package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===== Relationships ===== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = false)
    private FieldExecutive fieldExecutive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = true)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", nullable = true)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stockist_id", nullable = true)
    private Stockist stockist;

    @OneToMany(
    mappedBy = "visit",
    cascade = CascadeType.ALL,
    orphanRemoval = true
    )
    @Builder.Default
    private List<ConvertedProduct> convertedProducts = List.of();

    @OneToOne(mappedBy = "originalVisit", fetch = FetchType.LAZY)
    private ManagerVisit managerVisit;



    /* ===== Planning (Slot logic) ===== */

    @Column(nullable = false)
    private LocalDate visitDate;

    @Column(nullable = false)
    private Integer weekNumber;

    @Column(nullable = false)
    private Integer dayOfWeek; // 1–7

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VisitStatus status = VisitStatus.SCHEDULED;

    /* ===== Visit Execution ===== */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitType visitType; // DOCTOR, PHARMACIST, STOCKIST

    private LocalDateTime actualVisitTime;

    private String location;
    private String notes;

    @ElementCollection
    @CollectionTable(name = "visit_activities", joinColumns = @JoinColumn(name = "visit_id"))
    @Column(name = "activity")
    @Builder.Default
    private List<String> activitiesPerformed = List.of();

    /* ===== Pharmacy / Stockist ===== */
    private String pharmacyName;
    private String contactPerson;
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "stockist_type", length = 20)
    private StockistType stockistType;

    private String stockistName;
    private Double orderValue;

    /* ===== Audit ===== */

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(name = "scheduled_date", nullable = true)
    private LocalDateTime scheduledDate;

    private LocalDateTime actualDate;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* ===== Enums ===== */

    public enum VisitType {
        DOCTOR, PHARMACIST, STOCKIST
    }

    public enum VisitStatus {
        SCHEDULED,     // slot created
        APPROVED,    // manager approved
        REJECTED,    // rejected slot
        COMPLETED,   // visit done
        MISSED       // visit not done
    }



    public enum StockistType {
        NONE, SUB_STOCKIST, SUPER_STOCKIST
    }
}
