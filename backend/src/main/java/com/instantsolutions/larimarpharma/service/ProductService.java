package com.instantsolutions.larimarpharma.service;

import com.instantsolutions.larimarpharma.DTOs.ProductRequestDto;
import com.instantsolutions.larimarpharma.entity.Product;
import com.instantsolutions.larimarpharma.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(ProductRequestDto dto) {

        Product product = Product.builder()
                .name(dto.getName().toUpperCase())
                .category(dto.getCategory().toUpperCase())
                .description(dto.getDescription())
                .ptr(dto.getPtr())
                .pts(dto.getPts())
                .price(dto.getPrice())
                .active(dto.isActive())
                .pts(dto.getPts())
                .newPts(dto.getNewPts())
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequestDto dto) {

        Product product = getProductById(id);

        product.setName(dto.getName().toUpperCase());
        product.setCategory(dto.getCategory().toUpperCase());
        product.setDescription(dto.getDescription());
        product.setPtr(dto.getPtr());
        product.setPts(dto.getPts());
        product.setPrice(dto.getPrice());
        product.setActive(dto.isActive());
        product.setNewPts(dto.getNewPts());

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
