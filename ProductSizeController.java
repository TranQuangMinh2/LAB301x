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
public class ProductSizeController {
	@Autowired
	private ProductSizeService productSizeService;
	@Autowired
	private ProductService productService;
	
    @GetMapping("/productSize")
    public String getProductSize(Model model) {
        List<ProductSize> productSize = productSizeService.getAllProducts();
        model.addAttribute("productSize", productSize);
        return "admin/productSize";
    }
	
    @GetMapping("/addProductSize")
    public String showAddProductSizeForm(Model model) {
        ProductSize productSize = new ProductSize();
        List<Product> products = productService.getAllProducts();
        model.addAttribute("productSize", productSize);
        model.addAttribute("products", products); 
        return "admin/add-product-size";
    }
    
    @PostMapping("/addProductSize")
    public String addSize(@ModelAttribute ProductSize productSize) {
    	productSizeService.saveProductSize(productSize);
        return "redirect:/admin";
    }
	
	@GetMapping("/updateProductSize/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
	    ProductSize productSize = productSizeService.getProductSizeById(id);
	    model.addAttribute("productSize", productSize);
	    return "admin/update-product-size";
	}

	@PostMapping("/updateProductSize/{id}")
	public String updateProduct(@PathVariable("id") long id, ProductSize productSize, Model model) {
		productSizeService.updateProductSize(productSize);
        List<ProductSize> productSizes = productSizeService.getAllProducts();
        model.addAttribute("productSizes", productSizes);
	    return "redirect:/admin";
	}
	
}
