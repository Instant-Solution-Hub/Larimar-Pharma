package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.StockistRequestDto;
import com.instantsolutions.larimarpharma.DTOs.StockistResponseDto;
import com.instantsolutions.larimarpharma.entity.FieldExecutive;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.entity.Stockist;
import com.instantsolutions.larimarpharma.entity.StockistProductStock;
import com.instantsolutions.larimarpharma.repository.FieldExecutiveRepository;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import com.instantsolutions.larimarpharma.repository.StockistProductStockRepository;
import com.instantsolutions.larimarpharma.repository.StockistRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockistService {

    private final StockistRepository stockistRepository;

    @Autowired
    private FieldExecutiveRepository fieldExecutiveRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockistProductStockRepository stockRepository;

    public StockistResponseDto createStockist(StockistRequestDto dto) {
        Stockist stockist = Stockist.builder()
                .name(dto.getName().toUpperCase())
                .type(dto.getType())
                .contactPerson(dto.getContactPerson())
                .contactNumber(dto.getContactNumber())
                .location(dto.getLocation())
                .active(true)
                .build();

        return mapToDto(stockistRepository.save(stockist));
    }

    public StockistResponseDto updateStockist(Long id, StockistRequestDto dto) {
        Stockist stockist = getEntity(id);

        stockist.setName(dto.getName().toUpperCase());
        stockist.setType(dto.getType());
        stockist.setContactPerson(dto.getContactPerson());
        stockist.setContactNumber(dto.getContactNumber());
        stockist.setLocation(dto.getLocation());

        return mapToDto(stockistRepository.save(stockist));
    }

    public StockistResponseDto getStockistById(Long id) {
        return mapToDto(getEntity(id));
    }

    public List<StockistResponseDto> getAllStockists() {
        return stockistRepository.findByActiveTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void deleteStockist(Long id) {
        Stockist stockist = getEntity(id);
        stockist.setActive(false); // soft delete
        stockistRepository.save(stockist);
    }

    // 🔹 Helpers
    private Stockist getEntity(Long id) {
        return stockistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stockist not found"));
    }

    private StockistResponseDto mapToDto(Stockist stockist) {
        return StockistResponseDto.builder()
                .id(stockist.getId())
                .name(stockist.getName())
                .type(stockist.getType())
                .contactPerson(stockist.getContactPerson())
                .contactNumber(stockist.getContactNumber())
                .location(stockist.getLocation())
                .active(stockist.isActive())
                .createdAt(stockist.getCreatedAt())
                .updatedAt(stockist.getUpdatedAt())
                .build();
    }

    public void assignFieldExecutives(Long stockistId, Set<Long> feIds) {
        Stockist stockist = getEntity(stockistId);

        Set<FieldExecutive> executives =
                new HashSet<>(fieldExecutiveRepository.findAllById(feIds));

        stockist.getFieldExecutives().addAll(executives);

        stockistRepository.save(stockist);
    }

    public void removeFieldExecutive(Long stockistId, Long feId) {
        Stockist stockist = getEntity(stockistId);

        stockist.getFieldExecutives()
                .removeIf(fe -> fe.getId().equals(feId));

        stockistRepository.save(stockist);
    }

    public void addOrUpdateProductStock(Long stockistId, Long productId, Integer quantity) {

        Stockist stockist = getEntity(stockistId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        StockistProductStock stock =
                stockRepository.findByStockistIdAndProductId(stockistId, productId)
                        .orElse(
                                StockistProductStock.builder()
                                        .stockist(stockist)
                                        .product(product)
                                        .build()
                        );

        stock.setAvailableQuantity(quantity);
        stockRepository.save(stock);
    }

    public void removeProductFromStockist(Long stockistId, Long productId) {
        StockistProductStock stock =
                stockRepository.findByStockistIdAndProductId(stockistId, productId)
                        .orElseThrow(() -> new EntityNotFoundException("Stock not found"));

        stockRepository.delete(stock);
    }


}
