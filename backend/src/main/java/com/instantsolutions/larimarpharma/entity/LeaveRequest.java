package com.instantsolutions.larimarpharma.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = true)
    @JsonIgnore
    private FieldExecutive fieldExecutive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = true)
    @JsonIgnore
    private Manager manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = true)
    @JsonIgnore
    private Admin admin;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDateTime fromDate;

    @Column(nullable = false)
    private LocalDateTime toDate;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(updatable = false)
    private LocalDateTime appliedDate;

    private LocalDateTime approvalDate;

    @PrePersist
    protected void onCreate() {
        appliedDate = LocalDateTime.now();
        validateApplicant();
    }

    @PreUpdate
    protected void onUpdate() {
        validateApplicant();
    }

    private void validateApplicant() {
        if (fieldExecutive == null && manager == null && admin == null) {
            throw new IllegalStateException(
                    "Leave request must be associated with either a Field Executive or a Manager or an Admin"
            );
        }
    }

    public enum LeaveType {
        SICK_LEAVE, CASUAL_LEAVE, EARNED_LEAVE
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
}
