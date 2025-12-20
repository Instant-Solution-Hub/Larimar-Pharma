package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.PromotionRequestDto;
import com.instantsolutions.larimarpharma.entity.Promotion;
import com.instantsolutions.larimarpharma.repository.PromotionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public Promotion createPromotion(PromotionRequestDto dto) {
        Promotion promotion = Promotion.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .type(dto.getType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .active(dto.isActive())
                .build();

        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Long id, PromotionRequestDto dto) {
        Promotion promotion = getPromotionById(id);

        promotion.setName(dto.getName());
        promotion.setDescription(dto.getDescription());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setActive(dto.isActive());

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
}
