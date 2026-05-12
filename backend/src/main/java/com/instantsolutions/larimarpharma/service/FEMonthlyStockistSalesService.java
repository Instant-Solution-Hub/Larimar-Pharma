package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.MonthlyStockistSalesRowDto;
import com.instantsolutions.larimarpharma.DTOs.MonthlyStockistSalesSummaryDto;
import com.instantsolutions.larimarpharma.entity.FEMonthlyStockistSales;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Stockist;
import com.instantsolutions.larimarpharma.repository.FEMonthlyStockistSalesRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.StockistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FEMonthlyStockistSalesService {

    private final FEMonthlyStockistSalesRepository repository;
    private final FieldExecutiveRepository feRepository;
    private final StockistRepository stockistRepository;

    // 🔹 GET current month sales
    @Transactional
    public List<MonthlyStockistSalesRowDto> getMonthlySales(Long feId) {

        YearMonth ym = YearMonth.now();
        int year = ym.getYear();
        int month = ym.getMonthValue();

        // 🔹 Fetch FE
        FieldExecutive fe = feRepository.findById(feId)
                .orElseThrow(() -> new RuntimeException("FE not found"));

        // 🔹 Get ALL stockists (IMPORTANT CHANGE)
        List<Stockist> allStockists = stockistRepository.findAll();

        // 🔹 Get existing sales for this month
        List<FEMonthlyStockistSales> existingSales =
                repository.findByFieldExecutiveIdAndYearAndMonth(
                        feId, year, month
                );

        // 🔹 Map for quick lookup
        Map<Long, FEMonthlyStockistSales> salesMap =
                existingSales.stream()
                        .collect(Collectors.toMap(
                                s -> s.getStockist().getId(),
                                Function.identity()
                        ));

        List<FEMonthlyStockistSales> toSave = new ArrayList<>();
        List<MonthlyStockistSalesRowDto> response = new ArrayList<>();

        // 🔹 Ensure every stockist has a row
        for (Stockist stockist : allStockists) {

            FEMonthlyStockistSales sale = salesMap.get(stockist.getId());

            if (sale == null) {
                // ✅ CREATE missing row
                sale = FEMonthlyStockistSales.builder()
                        .fieldExecutive(fe)
                        .stockist(stockist)
                        .year(year)
                        .month(month)
                        .price(0.0)
                        .build();

                toSave.add(sale);
            }

            response.add(
                    MonthlyStockistSalesRowDto.builder()
                            .stockistId(stockist.getId())
                            .stockistName(stockist.getName())
                            .price(sale.getPrice() == null ? 0.0 : sale.getPrice())
                            .build()
            );
        }

        // 🔹 Bulk save missing rows (efficient)
        if (!toSave.isEmpty()) {
            repository.saveAll(toSave);
        }

        return response;
    }

    @Transactional
    public List<MonthlyStockistSalesSummaryDto> getAllFESummary(
            int year,
            int month
    ) {
        return repository
                .findAllSummaryByMonthAndYear(year, month);
    }

    @Transactional
    public List<MonthlyStockistSalesSummaryDto> getManagerFESummary(
            Long managerId,
            int year,
            int month
    ) {
        return repository
                .findAllSummaryByManagerAndMonthAndYear(managerId, year, month);
    }

    // 🔹 UPDATE price
    public void updatePrice(Long feId, Long stockistId, Double price) {

        YearMonth ym = YearMonth.now();

        FEMonthlyStockistSales entity =
                repository
                        .findByFieldExecutiveIdAndStockistIdAndYearAndMonth(
                                feId,
                                stockistId,
                                ym.getYear(),
                                ym.getMonthValue()
                        )
                        .orElseGet(() -> {

                            FieldExecutive fe = feRepository.findById(feId)
                                    .orElseThrow(() -> new RuntimeException("FE not found"));

                            Stockist stockist = stockistRepository.findById(stockistId)
                                    .orElseThrow(() -> new RuntimeException("Stockist not found"));

                            return FEMonthlyStockistSales.builder()
                                    .fieldExecutive(fe)
                                    .stockist(stockist)
                                    .year(ym.getYear())
                                    .month(ym.getMonthValue())
                                    .build();
                        });

        entity.setPrice(price);

        repository.save(entity);
    }
}
