package com.trnqngmnh.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.trnqngmnh.library.config.BookProcessor;
import com.trnqngmnh.library.entity.Brand;
import com.trnqngmnh.library.entity.Category;
import com.trnqngmnh.library.repository.ProductSizeRepository;
import com.trnqngmnh.library.service.BrandService;
import com.trnqngmnh.library.service.CategoryService;
import com.trnqngmnh.library.service.OpenLibraryService;

@Controller
public class BookController {

	@Autowired
	private OpenLibraryService openLibraryService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	ProductSizeRepository productSizeRepository;

	@GetMapping("/searchBook")
	public String searchBooks(@RequestParam String title, @RequestParam(required = false) Integer page, Model model) {
		String jsonResponse;
		if (page != null) {
			jsonResponse = openLibraryService.searchBooksByTitleWithPage(title, page);
		} else {
			jsonResponse = openLibraryService.searchBooksByTitle(title);
		}

		BookProcessor bookProcessor = new BookProcessor();
		JsonNode books = bookProcessor.processSearchResults(jsonResponse);

		model.addAttribute("books", books);
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		return "shopper/bookDetails";
	}
}
