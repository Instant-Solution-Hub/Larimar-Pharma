package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "field_executive_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldExecutiveProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = false, unique = true)
    private FieldExecutive fieldExecutive;

    private Integer attendancePercentage;
    private Double targetAchieved; // percentage
    private Double incentiveEarned;
    private Integer totalLeaves;
    private Integer leavesTaken;
    private Integer leavesRemaining;

    // Monthly targets
    private Integer aPlusDoctorTarget = 90; // 30 doctors × 3 visits
    private Integer aDoctorTarget = 120;    // 60 doctors × 2 visits
    private Integer bDoctorTarget = 10;     // 10 doctors × 1 visit

    private Integer pharmacyVisitTarget;
    private Integer stockistVisitTarget;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

