package com.instantsolutions.larimarpharma.DTOs;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OfferRequestDto {
    private String offerCategory;
    private String offerName;
    private boolean active = true;
    private MultipartFile image;
}
