package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.trnqngmnh.library.entity.Category;
import com.trnqngmnh.library.entity.ResourceNotFoundException;
import com.trnqngmnh.library.repository.CategoryRepository;
import com.trnqngmnh.library.service.CategoryService;

class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryService categoryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindCategoryById_Success() {
		// Arrange
		Long categoryId = 1L;
		Category category = new Category();
		category.setId(categoryId);
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

		// Act
		Category result = categoryService.findCategoryById(categoryId);

		// Assert
		assertEquals(category, result);
		verify(categoryRepository, times(1)).findById(categoryId);
	}

	@Test
	void testFindCategoryById_NotFound() {
		// Arrange
		Long categoryId = 1L;
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(ResourceNotFoundException.class, () -> categoryService.findCategoryById(categoryId));
		verify(categoryRepository, times(1)).findById(categoryId);
	}

	@Test
	void testFindAllCategorys() {
		// Arrange
		List<Category> categories = new ArrayList<>();
		categories.add(new Category());
		when(categoryRepository.findAll()).thenReturn(categories);

		// Act
		List<Category> result = categoryService.findAllCategorys();

		// Assert
		assertEquals(categories, result);
		verify(categoryRepository, times(1)).findAll();
	}

	@Test
	void testSaveCategory() {
		// Arrange
		Category category = new Category();
		when(categoryRepository.save(category)).thenReturn(category);

		// Act
		Category result = categoryService.saveCategory(category);

		// Assert
		assertEquals(category, result);
		verify(categoryRepository, times(1)).save(category);
	}

	@Test
	void testUpdateCategory_Success() {
		// Arrange
		Long categoryId = 1L;
		Category existingCategory = new Category();
		existingCategory.setId(categoryId);
		existingCategory.setName("Old Name");

		Category updatedCategory = new Category();
		updatedCategory.setId(categoryId);
		updatedCategory.setName("New Name");

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
		when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

		// Act
		Category result = categoryService.updateCategory(updatedCategory);

		// Assert
		assertEquals("New Name", result.getName());
		verify(categoryRepository, times(1)).findById(categoryId);
		verify(categoryRepository, times(1)).save(existingCategory);
	}

	@Test
	void testUpdateCategory_NotFound() {
		// Arrange
		Long categoryId = 1L;
		Category category = new Category();
		category.setId(categoryId);
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NullPointerException.class, () -> categoryService.updateCategory(category));
		verify(categoryRepository, times(1)).findById(categoryId);
		verify(categoryRepository, never()).save(any());
	}

	@Test
	void testDeleteById() {
		// Arrange
		Long categoryId = 1L;

		// Act
		categoryService.deleteById(categoryId);

		// Assert
		verify(categoryRepository, times(1)).deleteById(categoryId);
	}
}
