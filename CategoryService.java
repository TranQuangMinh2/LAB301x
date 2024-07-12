package com.trnqngmnh.library;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	@Autowired
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public Category findCategoryById(Long id) {
		return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
	}

	public List<Category> findAllCategorys() {
		// TODO Auto-generated method stub
		return categoryRepository.findAll();
	}

	public Category saveCategory(Category category) {
		// TODO Auto-generated method stub
		return categoryRepository.save(category);

	}

	public Category updateCategory(Category category) {
		Category existingCategory = categoryRepository.findById(category.getId()).orElse(null);
		existingCategory.setName(category.getName());
		return categoryRepository.save(existingCategory);
	}

	public void deleteById(Long id) {
		categoryRepository.deleteById(id);
	}
}