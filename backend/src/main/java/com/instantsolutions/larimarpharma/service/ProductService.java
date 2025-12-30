package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.ProductRequestDto;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductRequestDto dto) {

        Product product = Product.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .ptr(dto.getPtr())
                .pts(dto.getPts())
                .price(dto.getPrice())
                .active(dto.isActive())
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequestDto dto) {

        Product product = getProductById(id);

        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setDescription(dto.getDescription());
        product.setPtr(dto.getPtr());
        product.setPts(dto.getPts());
        product.setPrice(dto.getPrice());
        product.setActive(dto.isActive());

        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found with id: " + id)
                );
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }
}
