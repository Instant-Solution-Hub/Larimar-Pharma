package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "zsm_fe_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZsmFieldExecutiveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===== REQUESTER ===== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zsm_id", nullable = false)
    private Admin zsmAdmin;

    /* ===== REQUESTED NEW FE ===== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_fe_id", nullable = false)
    private FieldExecutive requestedFieldExecutive;

    /* ===== CURRENT FE BEING REPLACED (optional) ===== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_fe_id", nullable = true)
    private FieldExecutive currentFieldExecutive;

    /* ===== TARGET SLOT ===== */
    @Column(nullable = false)
    private Integer weekNumber;

    @Column(nullable = false)
    private Integer dayOfWeek;

    @Column(nullable = false)
    private LocalDate targetDate;

    /* ===== REQUEST DATA ===== */
    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Column(length = 1000)
    private String adminRemarks;

    /* ===== APPROVAL TRACKING ===== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_admin_id")
    private Admin reviewedBy;

    private LocalDateTime reviewedAt;

    /* ===== TIMESTAMPS ===== */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }
}