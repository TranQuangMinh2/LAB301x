package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.trnqngmnh.library.entity.ProductImage;
import com.trnqngmnh.library.repository.ProductImageRepository;
import com.trnqngmnh.library.service.ProductImageService;

class ProductImageServiceTest {

	@Mock
	private ProductImageRepository productImageRepository;

	@InjectMocks
	private ProductImageService productImageService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSaveProductImage_Success() {
		ProductImage productImage = new ProductImage();
		productImage.setId(1L);
		productImage.setPath("path/to/image");
		productImage.setIsPrimary(true);

		when(productImageRepository.save(productImage)).thenReturn(productImage);

		ProductImage result = productImageService.saveProductImage(productImage);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("path/to/image", result.getPath());
		assertTrue(result.getIsPrimary());
	}

	@Test
	void testGetAllProducts_Success() {
		ProductImage image1 = new ProductImage();
		ProductImage image2 = new ProductImage();

		when(productImageRepository.findAll()).thenReturn(Arrays.asList(image1, image2));

		List<ProductImage> result = productImageService.getAllProducts();

		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	void testGetProductImageById_Found() {
		ProductImage productImage = new ProductImage();
		productImage.setId(1L);

		when(productImageRepository.findById(1L)).thenReturn(Optional.of(productImage));

		ProductImage result = productImageService.getProductImageById(1L);

		assertNotNull(result);
		assertEquals(1L, result.getId());
	}

	@Test
	void testGetProductImageById_NotFound() {
		when(productImageRepository.findById(1L)).thenReturn(Optional.empty());

		ProductImage result = productImageService.getProductImageById(1L);

		assertNull(result);
	}

	@Test
	void testUpdateProductImage_Success() {
		ProductImage existingProductImage = new ProductImage();
		existingProductImage.setId(1L);
		existingProductImage.setPath("old/path");
		existingProductImage.setIsPrimary(false);

		ProductImage updatedProductImage = new ProductImage();
		updatedProductImage.setId(1L);
		updatedProductImage.setPath("new/path");
		updatedProductImage.setIsPrimary(true);

		when(productImageRepository.findById(1L)).thenReturn(Optional.of(existingProductImage));
		when(productImageRepository.save(existingProductImage)).thenReturn(updatedProductImage);

		ProductImage result = productImageService.updateProductImage(updatedProductImage);

		assertNotNull(result);
		assertEquals("new/path", result.getPath());
		assertTrue(result.getIsPrimary());
	}

	@Test
	void testDeleteById_Success() {
		Long id = 1L;

		doNothing().when(productImageRepository).deleteById(id);

		productImageService.deleteById(id);

		verify(productImageRepository, times(1)).deleteById(id);
	}
}
