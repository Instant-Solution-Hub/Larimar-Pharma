package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "field_executives")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"visits", "slots", "orders", "managerJoinings"})
public class FieldExecutive extends BaseUser {

    @OneToOne(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FieldExecutiveProfile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    private String employeeCode;
    private String territory;
    private String region;

    // Visits conducted by this FE
    @OneToMany(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Visit> visits = new HashSet<>();

    // Slots planned by this FE
    @OneToMany(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Slot> slots = new HashSet<>();

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

    // Leave requests by this FE
    @OneToMany(mappedBy = "fieldExecutive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<LeaveRequest> leaveRequests = new HashSet<>();

}

