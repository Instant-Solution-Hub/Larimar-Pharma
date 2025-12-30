package com.instantsolutions.larimarpharma.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String name;

    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String category;

    @NotBlank(message = "is required")
    @Pattern(regexp = ".*\\S.*", message = "cannot contain only spaces")
    private String description;

    private Double price;
    private Double ptr;
    private Double pts;
    private boolean active;

    // Trim setters
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}
