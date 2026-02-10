package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "slot_change_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = true)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_visit_id", nullable = true)
    private ManagerVisit managerVisit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_manager_id", nullable = true)
    private Manager requestedManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_fe_id", nullable = true)
    private FieldExecutive requestedFieldExecutive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id", nullable = true)
    private Admin approvedBy; // Admin user

    @Column(nullable = false)
    private LocalDate currentVisitDate;

    @Column(nullable = false)
    private Integer currentWeekNumber;

    @Column(nullable = false)
    private Integer currentDayOfWeek;

    @Column(nullable = false)
    private LocalDate requestedVisitDate;

    @Column(nullable = false)
    private Integer requestedWeekNumber;

    @Column(nullable = false)
    private Integer requestedDayOfWeek;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    private String adminNotes;

    @PreUpdate
    protected void onUpdate() {
        if (status != RequestStatus.PENDING && reviewedAt == null) {
            reviewedAt = LocalDateTime.now();
        }
    }

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }
}