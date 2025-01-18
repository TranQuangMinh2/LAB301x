package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.mock.web.MockMultipartFile;

import com.trnqngmnh.library.entity.Product;
import com.trnqngmnh.library.repository.ProductRepository;
import com.trnqngmnh.library.service.CSVService;

import jakarta.servlet.http.HttpServletResponse;

class CSVServiceTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private CSVService csvService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testExportToCSV_Success() throws IOException {
		// Arrange
		HttpServletResponse response = mock(HttpServletResponse.class);
		OutputStream outputStream = new ByteArrayOutputStream();
		when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(outputStream));

		List<Product> products = new ArrayList<>();
		Product product = new Product();
		product.setId(1L);
		product.setName("Test Product");
		product.setDescription("Test Description");
		product.setStatus("Available");
		product.setBrandId(2L);
		product.setCategoryId(3L);
		products.add(product);

		when(productRepository.findAll()).thenReturn(products);

		// Act
		csvService.exportToCSV(response);

		// Assert
		String expectedHeader = "ID,Tên,Mô tả,Trạng thái,Tác giả,Thể loại";
		String expectedContent = "1,Test Product,Test Description,Available,,";
		String actualOutput = outputStream.toString();
		assertTrue(actualOutput.contains(expectedHeader));
		assertTrue(actualOutput.contains(expectedContent));
		verify(productRepository, times(1)).findAll();
	}

	@Test
	void testSaveProductsFromCSV_Success() throws IOException {
		// Arrange
		String csvContent = """
				Name,Description,Price,Status,VersionName,BrandId,CategoryId
				Product1,Description1,100,Available,Version1,2,3
				Product2,Description2,200,Unavailable,Version2,4,5
				""";
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

		// Act
		csvService.saveProductsFromCSV(mockFile);

		// Assert
		verify(productRepository, times(1)).saveAll(anyList());
	}

	@Test
	void testSaveProductsFromCSV_InvalidData() throws IOException {
		// Arrange
		String csvContent = """
				Name,Description,Price,Status,VersionName,BrandId,CategoryId
				Product1,Description1,InvalidPrice,Available,Version1,2,3
				""";
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

		// Act
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> csvService.saveProductsFromCSV(mockFile));

		// Assert
		assertTrue(exception.getMessage().contains("Lỗi định dạng số"));
	}

	@Test
	void testSaveProductsFromCSV_EmptyFile() throws IOException {
		// Arrange
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.csv", "text/csv", "".getBytes());

		// Act & Assert
		assertDoesNotThrow(() -> csvService.saveProductsFromCSV(mockFile));
		verify(productRepository, times(0)).saveAll(anyList());
	}
}
