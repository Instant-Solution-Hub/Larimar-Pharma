package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.OfferRequestDto;
import com.instantsolutions.larimarpharma.DTOs.OfferResponseDto;
import com.instantsolutions.larimarpharma.entity.Offer;
import com.instantsolutions.larimarpharma.repository.OfferRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final FileStorageService fileStorageService;

    public OfferResponseDto createOffer(OfferRequestDto dto) throws IOException {
        String imageUrl =
                fileStorageService.storeFile(dto.getImage(), "offers");

        Offer offer = Offer.builder()
                .offerCategory(dto.getOfferCategory())
                .offerName(dto.getOfferName().toUpperCase())
                .imageUrl(imageUrl)
                .active(dto.isActive())
                .build();

        return mapToDto(offerRepository.save(offer));
    }

    public OfferResponseDto updateOffer(Long id, OfferRequestDto dto) throws IOException {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));

        offer.setOfferCategory(dto.getOfferCategory());
        offer.setOfferName(dto.getOfferName().toUpperCase());
        offer.setActive(dto.isActive());

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            offer.setImageUrl(fileStorageService.storeFile(dto.getImage(), "offers"));
        }

        return mapToDto(offerRepository.save(offer));
    }

    public OfferResponseDto getOfferById(Long id) {
        return offerRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));
    }

    public List<OfferResponseDto> getAllOffers() {
        return offerRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public void deleteOffer(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new EntityNotFoundException("Offer not found");
        }
        offerRepository.deleteById(id);
    }

    private OfferResponseDto mapToDto(Offer offer) {
        return OfferResponseDto.builder()
                .id(offer.getId())
                .offerCategory(offer.getOfferCategory())
                .offerName(offer.getOfferName())
                .imageUrl(offer.getImageUrl())
                .active(offer.isActive())
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .build();
    }
}
