package com.instantsolutions.larimarpharma.DTOs;

import com.instantsolutions.larimarpharma.entity.ConvertedProduct;
import com.instantsolutions.larimarpharma.entity.Visit;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MarkVisitRequestDto {

    @NotNull
    private Long visitId;

    private Long productId;

    @NotNull
    private Visit.VisitStatus status; // COMPLETED or MISSED

    private String locationMethod; // "gps" or "photo"

    // Base64 encoded image
    private String photoProof;

    private String latitude;
    private String longitude;

    private String notes;

    private List<String> activitiesPerformed;
    private List<ConvertedProductRequestDto> convertedProducts;
}
