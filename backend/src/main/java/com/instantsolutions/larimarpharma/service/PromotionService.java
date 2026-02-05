package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.PromotionCountResponseDto;
import com.instantsolutions.larimarpharma.DTOs.PromotionRequestDto;
import com.instantsolutions.larimarpharma.entity.Promotion;
import com.instantsolutions.larimarpharma.exceptions.BadRequestException;
import com.instantsolutions.larimarpharma.exceptions.ResourceNotFoundException;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import com.instantsolutions.larimarpharma.repository.PromotionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    private final ProductRepository productRepository;

    public Promotion createPromotion(PromotionRequestDto dto) {

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        if(!productRepository.existsByName(dto.getProduct())) throw new ResourceNotFoundException("No product found under the given name");
        Promotion promotion = Promotion.builder()
                .name(dto.getName().toUpperCase())
                .description(dto.getDescription())
                .type(dto.getType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .targetAudience(dto.getTargetAudience())
                .benefits(dto.getBenefits())
                .product(dto.getProduct())
                .build();

        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Long id, PromotionRequestDto dto) {

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        if(!productRepository.existsByName(dto.getProduct())) throw new ResourceNotFoundException("No product found under the given name");
        Promotion promotion = getPromotionById(id);


        promotion.setName(dto.getName().toUpperCase());
        promotion.setDescription(dto.getDescription());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setBenefits(dto.getBenefits());
        promotion.setTargetAudience(dto.getTargetAudience());
        promotion.setProduct(dto.getProduct());

        return promotionRepository.save(promotion);
    }

    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Promotion not found with id: " + id)
                );
    }

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public void deletePromotion(Long id) {
        Promotion promotion = getPromotionById(id);
        promotionRepository.delete(promotion);
    }

    public List<Promotion> getActiveAndUpcomingPromotions() {
        return promotionRepository.findActiveAndUpcoming(LocalDateTime.now());
    }

    public PromotionCountResponseDto getPromotionCounts() {

        LocalDateTime now = LocalDateTime.now();

        return PromotionCountResponseDto.builder()
                .totalPromotions(promotionRepository.count())
                .activePromotions(
                        promotionRepository.countActivePromotions(now))
                .upcomingPromotions(
                        promotionRepository.countUpcomingPromotions(now))
                .build();
    }

}
