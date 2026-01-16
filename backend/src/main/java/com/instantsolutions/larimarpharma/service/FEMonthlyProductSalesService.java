package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.MonthlySalesRowDto;
import com.instantsolutions.larimarpharma.entity.FEMonthlyProductSales;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FEMonthlyProductSalesRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FEMonthlyProductSalesService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    FEMonthlyProductSalesRepository salesRepository;

    @Autowired
    FieldExecutiveRepository fieldExecutiveRepository;

    public List<MonthlySalesRowDto> getMonthlySales(Long feId) {

        YearMonth ym = YearMonth.now();

        List<Product> products = productRepository.findAll();

        Map<Long, FEMonthlyProductSales> existing =
                salesRepository
                        .findByFieldExecutiveIdAndYearAndMonth(
                                feId, ym.getYear(), ym.getMonthValue()
                        )
                        .stream()
                        .collect(Collectors.toMap(
                                s -> s.getProduct().getId(),
                                s -> s
                        ));

        return products.stream().map(product -> {

            FEMonthlyProductSales row = existing.get(product.getId());
            double pts = product.getPts(); // or price field

            return MonthlySalesRowDto.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .pts(pts)
                    .quantity(row != null ? row.getQuantity() : 0)
                    .sales(row != null ? row.getSales() : 0)
                    .build();
        }).toList();
    }

    @Transactional
    public void updateQuantity(
            Long feId,
            Long productId,
            Integer quantity
    ) {
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity cannot be negative");

        YearMonth ym = YearMonth.now();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));


        FieldExecutive fieldExecutive = fieldExecutiveRepository.findById(feId)
                .orElseThrow(() -> new ResourceNotFoundException("Field Executive not found"));

        FEMonthlyProductSales sales =
                salesRepository
                        .findByFieldExecutiveIdAndProductIdAndYearAndMonth(
                                feId, productId, ym.getYear(), ym.getMonthValue()
                        )
                        .orElseGet(() -> FEMonthlyProductSales.builder()
                                .fieldExecutive(
                                      fieldExecutive)
                                .product(product)
                                .year(ym.getYear())
                                .month(ym.getMonthValue())
                                .pts(product.getPts())
                                .createdAt(LocalDateTime.now())
                                .build()
                        );

        sales.setQuantity(quantity);
        sales.setSales(quantity * sales.getPts());
        sales.setUpdatedAt(LocalDateTime.now());

        salesRepository.save(sales);
    }


}
