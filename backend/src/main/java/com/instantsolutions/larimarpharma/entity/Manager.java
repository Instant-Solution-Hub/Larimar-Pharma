package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "managers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"fieldExecutives", "approvalRequests"})
public class Manager extends BaseUser {

    @OneToOne(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ManagerProfile profile;

    private String employeeCode;
    private String department;
    private String designation;

    // Field Executives under this manager
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<FieldExecutive> fieldExecutives = new HashSet<>();

    // Approval requests handled by this manager
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ApprovalRequest> approvalRequests = new HashSet<>();

    // Manager joinings (when manager joins FE for visits)
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ManagerJoining> managerJoinings = new HashSet<>();

    // Territories managed by this manager
    @ElementCollection
    @CollectionTable(name = "manager_territories", joinColumns = @JoinColumn(name = "manager_id"))
    @Column(name = "territory")
    @Builder.Default
    private Set<String> managedTerritories = new HashSet<>();
}
