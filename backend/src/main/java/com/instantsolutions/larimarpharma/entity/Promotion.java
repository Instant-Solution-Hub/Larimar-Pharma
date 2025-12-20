package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Type type;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PromotionStatus status = PromotionStatus.UPCOMING;

    @Builder.Default
    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updateStatus();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateStatus();
    }

    private void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startDate)) {
            status = PromotionStatus.UPCOMING;
        } else if (now.isAfter(endDate)) {
            status = PromotionStatus.COMPLETED;
        } else {
            status = PromotionStatus.ACTIVE;
        }
    }

    public enum PromotionStatus {
        UPCOMING, ACTIVE, COMPLETED
    }

    public enum Type {
        NEW_PRODUCT, CAMPAIGN, OFFER
    }
}
