package com.trnqngmnh.library;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductColorService {
	private final ProductColorRepository productColorRepository;

	public ProductColorService(ProductColorRepository productColorRepository) {
		this.productColorRepository = productColorRepository;
	}

	public ProductColor saveProductColor(ProductColor productColor) {
		return productColorRepository.save(productColor);
	}

	public List<ProductColor> getAllProducts() {
		return productColorRepository.findAll();
	}

	public ProductColor getProductColorById(Long id) {
		return productColorRepository.findById(id).orElse(null);
	}

	public ProductColor updateProductColor(ProductColor productColor) {
		ProductColor existingProductColor = productColorRepository.findById(productColor.getId()).orElse(null);
		existingProductColor.setColorId(productColor.getColorId());
		return productColorRepository.save(existingProductColor);
	}

}
