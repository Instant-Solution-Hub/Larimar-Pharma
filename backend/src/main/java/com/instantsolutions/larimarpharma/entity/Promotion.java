package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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

    private String product;

    private List<String> targetAudience;

    private List<String> benefits;

    private Type type;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Builder.Default
    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

    }

    @Transient
    public PromotionStatus getStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startDate)) {
            return PromotionStatus.UPCOMING;
        } else if (now.isAfter(endDate)) {
            return PromotionStatus.COMPLETED;
        } else {
            return PromotionStatus.ACTIVE;
        }
    }

    public enum PromotionStatus {
        UPCOMING, ACTIVE, COMPLETED
    }

    public enum Type {
        NEW_PRODUCT, CAMPAIGN, OFFER
    }
}
