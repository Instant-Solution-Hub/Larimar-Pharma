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
    @JoinColumn(name = "field_executive_id", nullable = false)
    @JsonIgnore
    private FieldExecutive fieldExecutive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Manager approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType; // SICK, CASUAL, EARNED

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
    }

    public enum LeaveType {
        SICK_LEAVE, CASUAL_LEAVE, EARNED_LEAVE
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
}