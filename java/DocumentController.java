package com.trnqngmnh.library;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DocumentController {

	@Autowired
	private DocumentService documentService;

	@GetMapping("/document")
	public String getDocument(Model model) {
		List<Document> document = documentService.getAllDocuments();
		model.addAttribute("document", document);
		return "admin/document";
	}

	@GetMapping("/document2")
	public String getDocument2(HttpServletRequest request, Model model) {
		model.addAttribute("request", request);
		List<Document> documents = documentService.getAllProducts();
		model.addAttribute("documents", documents);
		return "shopper/document";
	}

	@GetMapping("/addDocument")
	public String showAddDocumentForm(Model model) {
		Document document = new Document();
		model.addAttribute("document", document);
		return "admin/add-document";
	}

	@PostMapping("/addDocument")
	public String addDocument(@ModelAttribute Document document) {
		documentService.saveDocument(document);
		return "redirect:/admin";
	}

	@GetMapping("/updateDocument/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		Document document = documentService.getDocumentById(id);
		model.addAttribute("document", document);
		return "admin/update-document";
	}

	@PostMapping("/updateDocument/{id}")
	public String updateDocument(@PathVariable("id") long id, @ModelAttribute Document document, Model model) {
		documentService.updateDocument(document);
		return "redirect:/admin";
	}

	@GetMapping("/document/delete/{id}")
	public String deleteDocument(@PathVariable Long id) {
		documentService.deleteById(id);
		return "redirect:/admin";
	}
}
