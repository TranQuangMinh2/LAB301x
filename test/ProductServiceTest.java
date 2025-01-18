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

import com.trnqngmnh.library.entity.Product;
import com.trnqngmnh.library.repository.ProductRepository;
import com.trnqngmnh.library.service.ProductService;

import jakarta.persistence.EntityManager;

class ProductServiceTest {

	@Mock
	private ProductRepository repository;

	@Mock
	private EntityManager entityManager;

	@InjectMocks
	private ProductService productService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSaveProduct_Success() {
		Product product = new Product();
		product.setId(1L);
		product.setName("Test Product");

		when(repository.save(product)).thenReturn(product);

		Product result = productService.saveProduct(product);

		assertNotNull(result);
		assertEquals("Test Product", result.getName());
		verify(repository, times(1)).save(product);
	}

	@Test
	void testGetAllProducts_Success() {
		Product product1 = new Product();
		product1.setId(1L);
		product1.setName("Product 1");

		Product product2 = new Product();
		product2.setId(2L);
		product2.setName("Product 2");

		when(repository.findAll()).thenReturn(Arrays.asList(product1, product2));

		List<Product> result = productService.getAllProducts();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Product 1", result.get(0).getName());
		assertEquals("Product 2", result.get(1).getName());
	}

	@Test
	void testFindProducts_SortByPriceAsc() {
		Product product1 = new Product();
		product1.setId(1L);
		product1.setPrice(10);

		Product product2 = new Product();
		product2.setId(2L);
		product2.setPrice(20);

		when(repository.findAllByOrderByPriceAsc()).thenReturn(Arrays.asList(product1, product2));

		List<Product> result = productService.findProducts("priceAsc");

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(10, result.get(0).getPrice());
		assertEquals(20, result.get(1).getPrice());
	}

	@Test
	void testFindProducts_InvalidSort() {
		List<Product> result = productService.findProducts("invalidSort");

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void testGetProductById_Found() {
		Product product = new Product();
		product.setId(1L);
		product.setName("Test Product");

		when(repository.findById(1L)).thenReturn(Optional.of(product));

		Product result = productService.getProductById(1L);

		assertNotNull(result);
		assertEquals("Test Product", result.getName());
	}

	@Test
	void testGetProductById_NotFound() {
		when(repository.findById(1L)).thenReturn(Optional.empty());

		Product result = productService.getProductById(1L);

		assertNull(result);
	}

	@Test
	void testDeleteProduct_Success() {
		doNothing().when(repository).deleteById(1L);

		productService.deleteProduct(1L);

		verify(repository, times(1)).deleteById(1L);
	}

	@Test
	void testSearchProducts_Success() {
		Product product1 = new Product();
		product1.setId(1L);
		product1.setName("Product 1");

		when(repository.findAll()).thenReturn(Arrays.asList(product1));

		List<Product> result = productService.searchProducts("Product", null, null);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Product 1", result.get(0).getName());
	}
}
