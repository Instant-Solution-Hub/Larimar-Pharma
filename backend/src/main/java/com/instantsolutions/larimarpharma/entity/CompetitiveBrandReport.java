package com.instantsolutions.larimarpharma.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "competitive_brand_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitiveBrandReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = false)
    private FieldExecutive fieldExecutive;

    @Column(nullable = false)
    private String brandName;

    @Column(nullable = false)
    private String companyName;


    private String productName;

    @Column(nullable = false)
    private String productCategory;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String designation;
    private String observations;
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean managerNotified = false;


    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

    }
}
