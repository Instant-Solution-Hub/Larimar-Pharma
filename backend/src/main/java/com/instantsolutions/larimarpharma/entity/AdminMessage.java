package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sender
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_executive_id", nullable = false)
    private FieldExecutive fieldExecutive;

    // Receiver
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "super_admin_id", nullable = false)
    private Admin superAdmin;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum MessageStatus {
        SENT,
        READ,
        RESOLVED
    }
}
