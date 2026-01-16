package com.instantsolutions.larimarpharma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "fe_monthly_product_sales",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "field_executive_id", "product_id", "year", "month"
                })
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FEMonthlyProductSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FieldExecutive fieldExecutive;

    @ManyToOne
    private Product product;

    private Integer year;     // 2026
    private Integer month;    // 1 - 12

    private Integer quantity; // editable by FE

    private Double pts;       // snapshot of PTS for that month
    private Double sales;     // qty * pts (derived)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
