package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "portal_lock_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortalLockStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = true)
    private Manager manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fe_id", nullable = true)
    private FieldExecutive fieldExecutive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType; // MANAGER or FIELD_EXECUTIVE

    @Column(nullable = false)
    private LocalDate lockedDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isLocked = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isUnlocked = false;

    private String lockReason;

    @Column(nullable = false)
    @Builder.Default
    private Boolean unlockRequested = false;

    @Enumerated(EnumType.STRING)
    private UnlockRequestStatus unlockRequestStatus;

    private LocalDateTime unlockRequestDate;

    private String unlockRequestReason;

    private LocalDateTime unlockApprovalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_admin_id")
    private Admin approvedBy;

    @PrePersist
    protected void onCreate() {
        if (isLocked && unlockRequested) {
            unlockRequestDate = LocalDateTime.now();
        }
    }

    public enum UserType {
        FIELD_EXECUTIVE, MANAGER, ADMIN
    }

    public enum UnlockRequestStatus {
        PENDING, APPROVED, REJECTED
    }
}