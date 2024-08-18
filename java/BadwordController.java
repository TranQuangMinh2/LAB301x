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
public class BadwordController {
	@Autowired
	private BadwordService badwordService;

	@GetMapping("/badword")
	public String getBadword(Model model) {
		List<Badword> badword = badwordService.findAllBadwords();
		model.addAttribute("badword", badword);
		return "admin/badword";
	}

	@GetMapping("/addBadword")
	public String showAddBadwordForm(Model model) {
		Badword badword = new Badword();
		model.addAttribute("badword", badword);
		return "admin/add-badword";
	}

	@PostMapping("/addBadword")
	public String addBadword(@ModelAttribute Badword badword) {
		badwordService.saveBadword(badword);
		return "redirect:/admin";
	}

	@GetMapping("/badword/{id}")
	public String showUpdateBadwordForm(@PathVariable("id") long id, Model model) {
		Badword badword = badwordService.findBadwordById(id);
		model.addAttribute("badword", badword);
		return "admin/update-badword";
	}

	@PostMapping("/badword/{id}")
	public String updateBadword(@PathVariable("id") long id, Badword badword, Model model) {
		badwordService.updateBadword(badword);
		return "redirect:/admin";
	}

	@GetMapping("/badword/delete/{id}")
	public String deleteBadword(@PathVariable Long id) {
		badwordService.deleteById(id);
		return "redirect:/admin";
	}
}
