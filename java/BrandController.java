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
public class BrandController {
	@Autowired
	private BrandService brandService;

	@GetMapping("/brand")
	public String getBrand(Model model) {
		List<Brand> brand = brandService.findAllBrands();
		model.addAttribute("brand", brand);
		return "admin/brand";
	}

	@GetMapping("/addBrand")
	public String showAddBrandForm(Model model) {
		Brand brand = new Brand();
		model.addAttribute("brand", brand);
		return "admin/add-brand";
	}

	@PostMapping("/addBrand")
	public String addBrand(@ModelAttribute Brand brand) {
		brandService.saveBrand(brand);
		return "redirect:/admin";
	}

	@GetMapping("/brand/{id}")
	public String showUpdateBrandForm(@PathVariable("id") long id, Model model) {
		Brand brand = brandService.findBrandById(id);
		model.addAttribute("brand", brand);
		return "admin/update-brand";
	}

	@PostMapping("/brand/{id}")
	public String updateBrand(@PathVariable("id") long id, Brand brand, Model model) {
		brandService.updateBrand(brand);
		return "redirect:/admin";
	}

	@GetMapping("/brand/delete/{id}")
	public String deleteBrand(@PathVariable Long id) {
		brandService.deleteById(id);
		return "redirect:/admin";
	}
}
