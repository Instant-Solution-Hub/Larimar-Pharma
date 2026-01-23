package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.MarketSalesDto;
import com.instantsolutions.larimarpharma.DTOs.UpdateMarketSalesRequestDto;
import com.instantsolutions.larimarpharma.entity.FEMarketMonthlySales;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FEMarketMonthlySalesRepository;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FEMarketSalesService {

    private final FEMarketMonthlySalesRepository salesRepo;
    private final FieldExecutiveRepository fieldExecutiveRepo;

    @Transactional
    public List<MarketSalesDto> getCurrentMonthMarketSales(Long feId) {

        FieldExecutive fe = fieldExecutiveRepo.findById(feId)
                .orElseThrow(() -> new ResourceNotFoundException("FE not found"));
        if (fe.getMarkets() == null) throw new ResourceNotFoundException("No markets allocated to the Field Executive");

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        // Existing sales
        List<FEMarketMonthlySales> existingSales =
                salesRepo.findByFieldExecutiveIdAndYearAndMonth(feId, year, month);

        Map<String, Double> salesMap = existingSales.stream()
                .collect(Collectors.toMap(
                        FEMarketMonthlySales::getMarket,
                        FEMarketMonthlySales::getSalesAmount
                ));
        System.out.println("Markets : " + fe.getMarkets());

        // Build response for ALL markets
        return fe.getMarkets().stream()
                .map(market -> MarketSalesDto.builder()
                        .market(market)
                        .salesAmount(salesMap.getOrDefault(market, 0.0))
                        .build())
                .toList();
    }

    @Transactional
    public MarketSalesDto updateMarketSales(
            Long feId,
            UpdateMarketSalesRequestDto request
    ) {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        FEMarketMonthlySales sales = salesRepo
                .findByFieldExecutiveIdAndMarketAndYearAndMonth(
                        feId, request.getMarket(), year, month
                )
                .orElseGet(() -> FEMarketMonthlySales.builder()
                        .fieldExecutive(
                                fieldExecutiveRepo.getReferenceById(feId)
                        )
                        .market(request.getMarket())
                        .year(year)
                        .month(month)
                        .salesAmount(0.0)
                        .build()
                );

        sales.setSalesAmount(request.getSalesAmount());
        salesRepo.save(sales);
        return MarketSalesDto.builder()
                .market(sales.getMarket())
                .salesAmount(sales.getSalesAmount())
                .build();
    }

}

