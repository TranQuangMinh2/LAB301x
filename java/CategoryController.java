package com.trnqngmnh.library;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/category")
	public String getCategory(Model model) {
		List<Category> category = categoryService.findAllCategorys();
		model.addAttribute("category", category);
		return "admin/category";
	}

	@GetMapping("/addCategory")
	public String showAddCategoryForm(Model model) {
		Category category = new Category();
		model.addAttribute("category", category);
		return "admin/add-category";
	}

	@PostMapping("/addCategory")
	public String addCategory(@ModelAttribute Category category) {
		categoryService.saveCategory(category);
		return "redirect:/admin";
	}

	@GetMapping("/category/{id}")
	public String showUpdateCategoryForm(@PathVariable("id") long id, Model model) {
		Category category = categoryService.findCategoryById(id);
		model.addAttribute("category", category);
		return "admin/update-category";
	}

	@PostMapping("/category/{id}")
	public String updateCategory(@PathVariable("id") long id, Category category, Model model) {
		categoryService.updateCategory(category);
		return "redirect:/admin";
	}

	@GetMapping("/category/delete/{id}")
	public String deleteCategory(@PathVariable Long id) {
		categoryService.deleteById(id);
		return "redirect:/admin";
	}
}
