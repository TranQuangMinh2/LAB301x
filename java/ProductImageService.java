package com.trnqngmnh.library;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductImageService {
	private final ProductImageRepository productImageRepository;

	public ProductImageService(ProductImageRepository productImageRepository) {
		this.productImageRepository = productImageRepository;
	}

	public ProductImage saveProductImage(ProductImage productImage) {
		return productImageRepository.save(productImage);
	}

	public List<ProductImage> getAllProducts() {
		return productImageRepository.findAll();
	}

	public ProductImage getProductImageById(Long id) {
		return productImageRepository.findById(id).orElse(null);
	}

	public ProductImage updateProductImage(ProductImage productImage) {
		ProductImage existingProductImage = productImageRepository.findById(productImage.getId()).orElse(null);
		existingProductImage.setIsPrimary(productImage.getIsPrimary());
		existingProductImage.setPath(productImage.getPath());
//		existingProductImage.setSize(productImage.getSize());
		existingProductImage.setProductId(productImage.getProductId());
		return productImageRepository.save(existingProductImage);
	}

	public void deleteById(Long id) {
		productImageRepository.deleteById(id);
	}
}
