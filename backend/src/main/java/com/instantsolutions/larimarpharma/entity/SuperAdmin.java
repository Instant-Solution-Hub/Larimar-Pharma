package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "super_admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SuperAdmin extends BaseUser {

//    @OneToOne(mappedBy = "superAdmin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private SuperAdminProfile profile;

    // System configurations created/updated by super admin
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<SystemConfiguration> systemConfigurations = new HashSet<>();

    // Audit logs viewed by super admin
//    @OneToMany(mappedBy = "viewedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<AuditLog> viewedAuditLogs = new HashSet<>();

    // Full system access
    @Builder.Default
    private boolean hasFullAccess = true;

    // Can create other admins
    @Builder.Default
    private boolean canCreateAdmins = true;
}