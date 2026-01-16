package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "fe_market_monthly_sales",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"field_executive_id", "market", "year", "month"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FEMarketMonthlySales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "field_executive_id")
    private FieldExecutive fieldExecutive;

    @Column(nullable = false)
    private String market;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month; // 1–12

    @Column(nullable = false)
    private Double salesAmount;
}

