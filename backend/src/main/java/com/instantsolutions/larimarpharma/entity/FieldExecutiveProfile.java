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
    private Integer casualLeaves;
    private Integer sickLeaves;


    // Monthly targets
    private Integer aPlusDoctorTarget ;
    private Integer aDoctorTarget ;
    private Integer bDoctorTarget ;

    private Integer pharmacyVisitProgress;
    private Integer stockistVisitProgress;
    private Integer doctorVisitProgress;

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

