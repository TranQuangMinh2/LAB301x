package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.trnqngmnh.library.entity.Brand;
import com.trnqngmnh.library.entity.ResourceNotFoundException;
import com.trnqngmnh.library.repository.BrandRepository;
import com.trnqngmnh.library.service.BrandService;

class BrandServiceTest {

	@Mock
	private BrandRepository brandRepository;

	@InjectMocks
	private BrandService brandService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindBrandById_WhenBrandExists() {
		Long brandId = 1L;
		Brand mockBrand = new Brand();
		mockBrand.setId(brandId);
		mockBrand.setName("Test Brand");

		when(brandRepository.findById(brandId)).thenReturn(Optional.of(mockBrand));

		Brand result = brandService.findBrandById(brandId);

		assertNotNull(result);
		assertEquals(brandId, result.getId());
		assertEquals("Test Brand", result.getName());
	}

	@Test
	void testFindBrandById_WhenBrandDoesNotExist() {
		Long brandId = 1L;
		when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> brandService.findBrandById(brandId));
	}

	@Test
	void testFindAllBrands() {
		Brand brand1 = new Brand();
		brand1.setId(1L);
		brand1.setName("Brand 1");

		Brand brand2 = new Brand();
		brand2.setId(2L);
		brand2.setName("Brand 2");

		List<Brand> mockBrands = Arrays.asList(brand1, brand2);

		when(brandRepository.findAll()).thenReturn(mockBrands);

		List<Brand> result = brandService.findAllBrands();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Brand 1", result.get(0).getName());
		assertEquals("Brand 2", result.get(1).getName());
	}

	@Test
	void testSaveBrand() {
		Brand newBrand = new Brand();
		newBrand.setName("New Brand");

		Brand savedBrand = new Brand();
		savedBrand.setId(1L);
		savedBrand.setName("New Brand");

		when(brandRepository.save(newBrand)).thenReturn(savedBrand);

		Brand result = brandService.saveBrand(newBrand);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("New Brand", result.getName());
	}

	@Test
	void testUpdateBrand() {
		Brand existingBrand = new Brand();
		existingBrand.setId(1L);
		existingBrand.setName("Old Brand");

		Brand updatedBrand = new Brand();
		updatedBrand.setId(1L);
		updatedBrand.setName("Updated Brand");

		when(brandRepository.findById(existingBrand.getId())).thenReturn(Optional.of(existingBrand));
		when(brandRepository.save(existingBrand)).thenReturn(updatedBrand);

		Brand result = brandService.updateBrand(updatedBrand);

		assertNotNull(result);
		assertEquals("Updated Brand", result.getName());
	}

	@Test
	void testDeleteById() {
		Long brandId = 1L;

		doNothing().when(brandRepository).deleteById(brandId);

		assertDoesNotThrow(() -> brandService.deleteById(brandId));
		verify(brandRepository, times(1)).deleteById(brandId);
	}
}
