package com.trnqngmnh.library.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.trnqngmnh.library.entity.ProductSize;
import com.trnqngmnh.library.repository.ProductSizeRepository;

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

	public void deleteProductSizeById(long id) {
		productSizeRepository.deleteById(id); // Xóa productSize theo id trong repository
	}

}
