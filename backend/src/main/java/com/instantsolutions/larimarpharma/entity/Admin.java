package com.instantsolutions.larimarpharma.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"createdUsers", "updatedPrograms"})
public class Admin extends BaseUser {

    @OneToOne(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AdminProfile profile;

    private String employeeCode;
    private String department;
    private String adminLevel; // Junior, Senior, Lead



//    // Doctor programs updated by this admin
//    @OneToMany(mappedBy = "updatedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<DoctorProgram> updatedPrograms = new HashSet<>();

    // Orders confirmed by this admin
    @OneToMany(mappedBy = "confirmedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Order> confirmedOrders = new HashSet<>();

    // Permissions
    @ElementCollection
    @CollectionTable(name = "admin_permissions", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "permission")
    @Builder.Default
    private Set<String> permissions = new HashSet<>();
}