package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OfferRequestDto {

    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String offerCategory;

    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String offerName;

    private boolean active = true;

    private MultipartFile image;

    // Trim setters (important)
    public void setOfferCategory(String offerCategory) {
        this.offerCategory = offerCategory == null ? null : offerCategory.trim();
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName == null ? null : offerName.trim();
    }
}
