package com.instantsolutions.larimarpharma.controller;

import com.instantsolutions.larimarpharma.DTOs.OfferRequestDto;
import com.instantsolutions.larimarpharma.DTOs.OfferResponseDto;
import com.instantsolutions.larimarpharma.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<OfferResponseDto> createOffer(
            @ModelAttribute OfferRequestDto dto) throws IOException {
        return ResponseEntity.ok(offerService.createOffer(dto));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<OfferResponseDto> updateOffer(
            @PathVariable Long id,
            @ModelAttribute OfferRequestDto dto) throws IOException {
        return ResponseEntity.ok(offerService.updateOffer(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferResponseDto> getOffer(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @GetMapping
    public ResponseEntity<List<OfferResponseDto>> getAllOffers() {
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }
}
