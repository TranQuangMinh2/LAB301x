package com.trnqngmnh.library;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
	List<Product> findAllByOrderByPriceAsc();

	List<Product> findAllByOrderByPriceDesc();

	List<Product> findByNameContaining(String text);

	List<Product> findByBrand(Brand brand);

	List<Object[]> findByStatus(String status);

	List<Object[]> findByName(String name);

	Page<Product> findAll(Pageable pageable);

	List<Product> findByCategory(Category category);
}