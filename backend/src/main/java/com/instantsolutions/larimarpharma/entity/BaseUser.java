package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    protected Long id;

    @Column(nullable = false, unique = true)
    protected String email;

    @Column(nullable = false)
    protected String password;

    @Column(nullable = false)
    protected String name;

    protected String phone;
    protected String emergencyContact;

    @Column(nullable = false)
    protected boolean active = true;

    @Column(updatable = false)
    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
