package com.trnqngmnh.library;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BrandService {

	private final BrandRepository brandRepository;

	public BrandService(BrandRepository brandRepository) {
		this.brandRepository = brandRepository;
	}

	public Brand findBrandById(Long id) {
		return brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
	}

	public List<Brand> findAllBrands() {
		// TODO Auto-generated method stub
		return brandRepository.findAll();
	}

	public Brand saveBrand(Brand brand) {
		// TODO Auto-generated method stub
		return brandRepository.save(brand);

	}

	public Brand updateBrand(Brand brand) {
		Brand existingBrand = brandRepository.findById(brand.getId()).orElse(null);
		existingBrand.setName(brand.getName());
		return brandRepository.save(existingBrand);
	}

	public void deleteById(Long id) {
		brandRepository.deleteById(id);
	}
}