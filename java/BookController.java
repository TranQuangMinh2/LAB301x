package com.trnqngmnh.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;

@Controller
public class BookController {

	@Autowired
	private OpenLibraryService openLibraryService;

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
		return "shopper/bookDetails";
	}
}
