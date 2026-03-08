package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "slot_planning_day_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotPlanningDayRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reason;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    private String adminNotes;
}
