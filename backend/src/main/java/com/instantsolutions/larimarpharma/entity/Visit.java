package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id" , nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = false)
    private FieldExecutive fieldExecutive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitType visitType; // DOCTOR, PHARMACIST, STOCKIST

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    private LocalDateTime actualDate;

    @Enumerated(EnumType.STRING)
    private VisitStatus status; // SCHEDULED, COMPLETED, MISSED

    private String location; // captured from FE's current location
    private String notes;

    @ElementCollection
    @CollectionTable(name = "visit_activities", joinColumns = @JoinColumn(name = "visit_id"))
    @Column(name = "activity")
    @Builder.Default
    private List<String> activitiesPerformed = List.of();

    @Column(nullable = false)
    @Builder.Default
    private boolean isScheduled = true;

    private String pharmacyName;
    private String contactPerson;
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    private StockistType stockistType;

    private String stockistName;
    private Double orderValue;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (actualDate == null && status == VisitStatus.COMPLETED) {
            actualDate = LocalDateTime.now();
        }
    }

    public enum VisitType {
        DOCTOR, PHARMACIST, STOCKIST
    }

    public enum VisitStatus {
        SCHEDULED, COMPLETED, MISSED
    }

    public enum StockistType {
        SUB_STOCKIST, SUPER_STOCKIST
    }
}

