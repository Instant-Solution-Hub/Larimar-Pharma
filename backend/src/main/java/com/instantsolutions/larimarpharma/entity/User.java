package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String employeeId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public enum UserRole {
        FIELD_EXECUTIVE, MANAGER, ADMIN
    }
}