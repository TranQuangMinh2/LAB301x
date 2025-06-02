package com.trnqngmnh.library.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trnqngmnh.library.entity.Brand;
import com.trnqngmnh.library.entity.Category;
import com.trnqngmnh.library.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
	List<Product> findAllByOrderByPriceAsc();

	List<Product> findAllByOrderByPriceDesc();

	List<Product> findByNameContaining(String text);

	List<Product> findByBrand(Brand brand);

	List<Object[]> findByStatus(String status);

	List<Object[]> findByName(String name);

	Page<Product> findAll(Pageable pageable);

	List<Product> findByCategory(Category category);

//	List<Product> findByBrandId(Long brandId);

	@Query("SELECT p FROM Product p WHERE p.brand.id = :brandId")
	List<Product> findByBrandId(@Param("brandId") Long brandId);

	@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
	List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

}