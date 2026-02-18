package com.instantsolutions.larimarpharma.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visual_aids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisualAid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // Visual Aid Name

    private String category;    // Category

    private String fileUrl;     // Stored file path

    private LocalDateTime uploadedAt;
}
