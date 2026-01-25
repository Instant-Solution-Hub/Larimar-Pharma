package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "field_executives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class FieldExecutive extends BaseUser {

    @OneToOne(mappedBy = "fieldExecutive", cascade = CascadeType.ALL,
               orphanRemoval = true , fetch = FetchType.LAZY)
    private FieldExecutiveProfile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String employeeCode;
    private String territory;
    private String region;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "field_executive_markets",
            joinColumns = @JoinColumn(name = "field_executive_id")
    )
    @Column(name = "market")
    @Builder.Default
    private List<String> markets = new ArrayList<>();


    // Visits planned and conducted by this FE
    @OneToMany(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Visit> visits = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "field_executive_stockists",
            joinColumns = @JoinColumn(name = "field_executive_id"),
            inverseJoinColumns = @JoinColumn(name = "stockist_id")
    )
    @Builder.Default
    private Set<Stockist> stockists = new HashSet<>();


    // Orders taken by this FE
    @OneToMany(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();

    // Manager joinings recorded by this FE
    @OneToMany(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ManagerJoining> managerJoinings = new HashSet<>();

    // Competitive brand reports by this FE
    @OneToMany(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<CompetitiveBrandReport> competitiveBrandReports = new HashSet<>();

    // Liquidation plans by this FE
    @OneToMany(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<LiquidationPlan> liquidationPlans = new HashSet<>();

    @OneToMany(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Doctor> doctors = new HashSet<>();



    // Leave requests by this FE
    @OneToMany(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<LeaveRequest> leaveRequests = new HashSet<>();

}

