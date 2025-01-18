package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
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

import com.trnqngmnh.library.entity.ProductSize;
import com.trnqngmnh.library.repository.ProductSizeRepository;
import com.trnqngmnh.library.service.ProductSizeService;

class ProductSizeServiceTest {

	@Mock
	private ProductSizeRepository productSizeRepository;

	@InjectMocks
	private ProductSizeService productSizeService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSaveProductSize_Success() {
		ProductSize productSize = new ProductSize();
		productSize.setId(1L);
		productSize.setQuantity(10);

		when(productSizeRepository.save(productSize)).thenReturn(productSize);

		ProductSize result = productSizeService.saveProductSize(productSize);

		assertNotNull(result);
		assertEquals("L", result.getSize());
		assertEquals(10, result.getQuantity());
		verify(productSizeRepository, times(1)).save(productSize);
	}

	@Test
	void testGetAllProducts_Success() {
		ProductSize productSize1 = new ProductSize();
		productSize1.setId(1L);
		productSize1.setQuantity(10);

		ProductSize productSize2 = new ProductSize();
		productSize2.setId(2L);
		productSize2.setQuantity(10);

		when(productSizeRepository.findAll()).thenReturn(Arrays.asList(productSize1, productSize2));

		List<ProductSize> result = productSizeService.getAllProducts();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("M", result.get(0).getSize());
		assertEquals("L", result.get(1).getSize());
	}

	@Test
	void testGetProductSizeById_Found() {
		ProductSize productSize = new ProductSize();
		productSize.setId(1L);
		productSize.setQuantity(10);

		when(productSizeRepository.findById(1L)).thenReturn(Optional.of(productSize));

		ProductSize result = productSizeService.getProductSizeById(1L);

		assertNotNull(result);
		assertEquals("L", result.getSize());
	}

	@Test
	void testGetProductSizeById_NotFound() {
		when(productSizeRepository.findById(1L)).thenReturn(Optional.empty());

		ProductSize result = productSizeService.getProductSizeById(1L);

		assertNull(result);
	}

	@Test
	void testUpdateProductSize_Success() {
		ProductSize existingProductSize = new ProductSize();
		existingProductSize.setId(1L);
		existingProductSize.setQuantity(5);

		ProductSize updatedProductSize = new ProductSize();
		updatedProductSize.setId(1L);
		updatedProductSize.setQuantity(10);

		when(productSizeRepository.findById(1L)).thenReturn(Optional.of(existingProductSize));
		when(productSizeRepository.save(any(ProductSize.class))).thenReturn(updatedProductSize);

		ProductSize result = productSizeService.updateProductSize(updatedProductSize);

		assertNotNull(result);
		assertEquals("L", result.getSize());
		assertEquals(10, result.getQuantity());
	}

	@Test
	void testUpdateProductSize_NotFound() {
		ProductSize updatedProductSize = new ProductSize();
		updatedProductSize.setId(1L);
		updatedProductSize.setQuantity(10);

		when(productSizeRepository.findById(1L)).thenReturn(Optional.empty());

		ProductSize result = productSizeService.updateProductSize(updatedProductSize);

		assertNull(result);
		verify(productSizeRepository, times(0)).save(any(ProductSize.class));
	}

	@Test
	void testDeleteProductSizeById_Success() {
		doNothing().when(productSizeRepository).deleteById(1L);

		productSizeService.deleteProductSizeById(1L);

		verify(productSizeRepository, times(1)).deleteById(1L);
	}
}
