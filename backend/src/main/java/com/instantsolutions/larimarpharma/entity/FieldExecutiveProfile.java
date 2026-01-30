package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "field_executive_profiles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "field_executive_id", "month", "year"
                })
        }
)
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


    private Integer month;
    private Integer year;

    private Integer attendancePercentage;
    private Double primaryTargetAchieved;
    private Double secondaryTargetAchieved;
    private Double primaryTargetSet;
    private Double secondaryTargetSet;
    private Double incentiveEarned;
    private Integer casualLeaves;
    private Integer sickLeaves;
    private Integer approvedCasualLeaves;
    private Integer approvedSickLeaves;


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

