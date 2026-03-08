package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "slot_planning_day_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotPlanningDayRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_manager_id", nullable = true)
    private Manager requestedManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_fe_id", nullable = true)
    private FieldExecutive requestedFieldExecutive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private LocalDate requestedAt = LocalDate.now();

    private LocalDate reviewedAt;

    @PreUpdate
    protected void onUpdate() {
        if (status != RequestStatus.PENDING && reviewedAt == null) {
            reviewedAt = LocalDate.now();
        }
    }

    private String adminNotes;
    public enum RequestStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }
}
