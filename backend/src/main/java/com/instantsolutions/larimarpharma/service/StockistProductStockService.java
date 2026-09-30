package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.StockistProductStockRequestDto;
import com.instantsolutions.larimarpharma.DTOs.StockistProductStockResponseDto;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.entity.Stockist;
import com.instantsolutions.larimarpharma.entity.StockistProductStock;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import com.instantsolutions.larimarpharma.repository.StockistProductStockRepository;
import com.instantsolutions.larimarpharma.repository.StockistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockistProductStockService {

    private final StockistProductStockRepository stockRepository;
    private final FieldExecutiveRepository fieldExecutiveRepository;
    private final StockistRepository stockistRepository;
    private final ProductRepository productRepository;


    /*
     * ADD STOCK
     */
    public StockistProductStockResponseDto addStock(
            Long feId,
            StockistProductStockRequestDto dto
    ) {

        validateQuantity(dto.getQuantity());

        FieldExecutive fieldExecutive =
                fieldExecutiveRepository.findById(feId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Field Executive not found with id: " + feId
                                )
                        );

        Stockist stockist =
                stockistRepository.findById(dto.getStockistId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Stockist not found with id: "
                                                + dto.getStockistId()
                                )
                        );

        Product product =
                productRepository.findById(dto.getProductId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: "
                                                + dto.getProductId()
                                )
                        );

        LocalDate currentMonth = getCurrentMonth();

        /*
         * IMPORTANT:
         *
         * Same FE + Stockist + Product + Month
         * cannot exist twice.
         */
        boolean alreadyExists =
                stockRepository
                        .findByFieldExecutiveIdAndStockistIdAndProductIdAndStockMonth(
                                feId,
                                dto.getStockistId(),
                                dto.getProductId(),
                                currentMonth
                        )
                        .isPresent();

        if (alreadyExists) {
            throw new BadRequestException(
                    "Stock already exists for this stockist and product "
                            + "for the current month"
            );
        }

        StockistProductStock stock =
                StockistProductStock.builder()
                        .fieldExecutive(fieldExecutive)
                        .stockist(stockist)
                        .product(product)
                        .stockMonth(currentMonth)
                        .quantity(dto.getQuantity())
                        .build();

        StockistProductStock saved =
                stockRepository.save(stock);

        return mapToResponse(saved);
    }


    /*
     * UPDATE STOCK
     */
    public StockistProductStockResponseDto updateStock(
            Long feId,
            Long stockId,
            StockistProductStockRequestDto dto
    ) {

        validateQuantity(dto.getQuantity());

        StockistProductStock stock =
                stockRepository.findByIdAndFieldExecutiveId(
                                stockId,
                                feId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Stock record not found for this Field Executive"
                                )
                        );

        /*
         * We don't allow changing FE, month, stockist or product
         * through update.
         *
         * The record represents the current month's
         * FE + Stockist + Product combination.
         *
         * Only quantity is updated.
         */
        stock.setQuantity(dto.getQuantity());

        StockistProductStock updated =
                stockRepository.save(stock);

        return mapToResponse(updated);
    }


    /*
     * DELETE STOCK
     */
    public void deleteStock(
            Long feId,
            Long stockId
    ) {

        StockistProductStock stock =
                stockRepository.findByIdAndFieldExecutiveId(
                                stockId,
                                feId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Stock record not found for this Field Executive"
                                )
                        );

        stockRepository.delete(stock);
    }


    /*
     * GET CURRENT MONTH STOCK
     *
     * FE + Product
     */
    @Transactional(readOnly = true)
    public List<StockistProductStockResponseDto>
    getCurrentMonthStockByFeAndProduct(
            Long feId,
            Long productId
    ) {

        validateFieldExecutive(feId);

        /*
         * Make sure product exists.
         */
        productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        LocalDate currentMonth = getCurrentMonth();

        return stockRepository
                .findByFieldExecutiveIdAndProductIdAndStockMonth(
                        feId,
                        productId,
                        currentMonth
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    /*
     * GET ALL CURRENT MONTH STOCK
     *
     * FE + all products + all stockists
     */
    @Transactional(readOnly = true)
    public List<StockistProductStockResponseDto>
    getCurrentMonthStockByFe(
            Long feId
    ) {

        validateFieldExecutive(feId);

        LocalDate currentMonth = getCurrentMonth();

        return stockRepository
                .findByFieldExecutiveIdAndStockMonth(
                        feId,
                        currentMonth
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    /*
     * GET CURRENT MONTH STOCK
     *
     * FE + Stockist
     */
    @Transactional(readOnly = true)
    public List<StockistProductStockResponseDto>
    getCurrentMonthStockByFeAndStockist(
            Long feId,
            Long stockistId
    ) {

        validateFieldExecutive(feId);

        LocalDate currentMonth = getCurrentMonth();

        return stockRepository
                .findByFieldExecutiveIdAndStockistIdAndStockMonth(
                        feId,
                        stockistId,
                        currentMonth
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    /*
     * HELPERS
     */

    private LocalDate getCurrentMonth() {
        return YearMonth.now().atDay(1);
    }


    private void validateQuantity(Integer quantity) {

        if (quantity == null) {
            throw new BadRequestException(
                    "Quantity is required"
            );
        }

        if (quantity < 0) {
            throw new BadRequestException(
                    "Quantity cannot be negative"
            );
        }
    }


    private void validateFieldExecutive(Long feId) {

        if (!fieldExecutiveRepository.existsById(feId)) {
            throw new ResourceNotFoundException(
                    "Field Executive not found with id: " + feId
            );
        }
    }


    private StockistProductStockResponseDto mapToResponse(
            StockistProductStock stock
    ) {

        return StockistProductStockResponseDto.builder()
                .id(stock.getId())

                .fieldExecutiveId(
                        stock.getFieldExecutive().getId()
                )

                .stockistId(
                        stock.getStockist().getId()
                )

                .stockistName(
                        stock.getStockist().getName()
                )

                .productId(
                        stock.getProduct().getId()
                )

                .productName(
                        stock.getProduct().getName()
                )

                .quantity(
                        stock.getQuantity()
                )

                .month(
                        YearMonth.from(
                                stock.getStockMonth()
                        ).toString()
                )

                .build();
    }
}