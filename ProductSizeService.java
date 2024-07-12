package com.trnqngmnh.library;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductSizeService {
    private final ProductSizeRepository productSizeRepository;

    public ProductSizeService(ProductSizeRepository productSizeRepository) {
        this.productSizeRepository = productSizeRepository;
    } 
    public ProductSize saveProductSize(ProductSize productSize) {
        return productSizeRepository.save(productSize);
    }
    public List<ProductSize> getAllProducts() {
        return productSizeRepository.findAll();
    }
    public ProductSize getProductSizeById(Long id) {
        return productSizeRepository.findById(id).orElse(null);
    }
    
    public ProductSize updateProductSize(ProductSize productSize) {
        ProductSize existingProductSize = productSizeRepository.findById(productSize.getId()).orElse(null);
        existingProductSize.setQuantity(productSize.getQuantity());
        existingProductSize.setSize(productSize.getSize());
        return productSizeRepository.save(existingProductSize);
    }

}
