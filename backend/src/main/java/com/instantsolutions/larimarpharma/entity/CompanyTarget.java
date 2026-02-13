package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "company_targets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"year", "month"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer year;

    private Integer month; // 1-12

    @Column(nullable = false)
    private BigDecimal targetAmount;

    @Column(nullable = false)
    private BigDecimal currentProgress;

}
