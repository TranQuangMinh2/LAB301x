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
public class ProductColorController {

	@Autowired
	private ProductColorService productColorService;
	@Autowired
	private ProductService productService;

	@GetMapping("/productColor")
	public String getProductColor(Model model) {
		List<ProductColor> productColor = productColorService.getAllProducts();
		model.addAttribute("productColor", productColor);
		return "admin/productColor";
	}

	@GetMapping("/addProductColor")
	public String showAddProductColorForm(Model model) {
		ProductColor productColor = new ProductColor();
		List<Product> products = productService.getAllProducts(); // Lấy tất cả các sản phẩm
		model.addAttribute("productColor", productColor);
		model.addAttribute("products", products); // Thêm danh sách sản phẩm vào model
		return "admin/add-product-color";
	}

	@PostMapping("/addProductColor")
	public String addColor(@ModelAttribute ProductColor productColor) {
		productColorService.saveProductColor(productColor);
		return "redirect:/admin";
	}

	@GetMapping("/updateProductColor/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		ProductColor productColor = productColorService.getProductColorById(id);
		model.addAttribute("productColor", productColor);
		return "admin/update-product-color";
	}

	@PostMapping("/updateProductColor/{id}")
	public String updateProduct(@PathVariable("id") long id, ProductColor productColor, Model model) {
		productColorService.updateProductColor(productColor);
		List<ProductColor> productColors = productColorService.getAllProducts();
		model.addAttribute("productColors", productColors);
		return "redirect:/admin";
	}

}
