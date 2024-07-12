package com.trnqngmnh.library;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProductImageController {
	@Autowired
	private ProductImageService productImageService;
	@Autowired
	private ProductService productService;
	@Autowired
	private UserService userService;

	@GetMapping("/productImage")
	public String getProductImage(Model model) {
		List<ProductImage> productImage = productImageService.getAllProducts();
		model.addAttribute("productImage", productImage);
		return "admin/productImage";
	}

	@GetMapping("/addProductImage")
	public String showAddProductImageForm(Model model) {
		ProductImage productImage = new ProductImage();
		model.addAttribute("productImage", productImage);
		List<Product> products = productService.getAllProducts();
		model.addAttribute("products", products);
		List<User> user = userService.getAllUsers();
		model.addAttribute("user", user);
		return "admin/add-product-image";
	}

	@PostMapping("/addProductImage")
	public String addImage(@ModelAttribute ProductImage productImage, @RequestParam("image") MultipartFile image) {
		try {
			byte[] bytes = image.getBytes();
			String absolutePath = new File("").getAbsolutePath();
			String filePath = absolutePath + File.separator + "src" + File.separator + "main" + File.separator
					+ "resources" + File.separator + "static" + File.separator + "images" + File.separator
					+ image.getOriginalFilename();
			Path path = Paths.get(filePath);
			Files.write(path, bytes);
			String relativePath = "/images/" + image.getOriginalFilename();
			productImage.setPath(relativePath);
			productImageService.saveProductImage(productImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/admin";
	}

	@GetMapping("/updateProductImage/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		ProductImage productImage = productImageService.getProductImageById(id);
		model.addAttribute("productImage", productImage);
		List<Product> products = productService.getAllProducts();
		model.addAttribute("products", products);
		return "admin/update-product-image";
	}

//	@PostMapping("/updateProductImage/{id}")
//	public String updateProduct(@PathVariable("id") long id, ProductImage productImage, Model model) {
//		productImageService.updateProductImage(productImage);
//        List<ProductImage> productImages = productImageService.getAllProducts();
//        model.addAttribute("productImages", productImages);
//	    return "redirect:/admin";
//	}
//	@PostMapping("/updateProductImage/{id}")
//	public String updateProduct(@PathVariable("id") long id, @ModelAttribute ProductImage productImage, @RequestParam("image") MultipartFile image, Model model) {
//	    try {
//	        byte[] bytes = image.getBytes();
//	        // Thay đổi đường dẫn tương đối thành đường dẫn tuyệt đối
//	        String absolutePath = new File("").getAbsolutePath();
//	        String filePath = absolutePath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "images" + File.separator + image.getOriginalFilename();
//	        Path path = Paths.get(filePath);
//	        Files.write(path, bytes);
//	        // Lưu đường dẫn tương đối vào cơ sở dữ liệu
//	        String relativePath = "/images/" + image.getOriginalFilename();
//	        productImage.setPath(relativePath);
//	        productImageService.updateProductImage(productImage);
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//
//	    List<ProductImage> productImages = productImageService.getAllProducts();
//	    model.addAttribute("productImages", productImages);
//	    return "redirect:/admin";
//	}
	@PostMapping("/updateProductImage/{id}")
	public String updateProduct(@PathVariable("id") long id, @ModelAttribute ProductImage productImage,
			@RequestParam(value = "image", required = false) MultipartFile image, Model model) {
		if (image != null && !image.isEmpty()) {
			try {
				byte[] bytes = image.getBytes();
				// Thay đổi đường dẫn tương đối thành đường dẫn tuyệt đối
				String absolutePath = new File("").getAbsolutePath();
				String filePath = absolutePath + File.separator + "src" + File.separator + "main" + File.separator
						+ "resources" + File.separator + "static" + File.separator + "images" + File.separator
						+ image.getOriginalFilename();
				Path path = Paths.get(filePath);
				Files.write(path, bytes);
				// Lưu đường dẫn tương đối vào cơ sở dữ liệu
				String relativePath = "/images/" + image.getOriginalFilename();
				productImage.setPath(relativePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		productImageService.updateProductImage(productImage);
		List<ProductImage> productImages = productImageService.getAllProducts();
		model.addAttribute("productImages", productImages);
		return "redirect:/admin";
	}

	@GetMapping("/productImage/delete/{id}")
	public String deleteProductImage(@PathVariable Long id) {
		productImageService.deleteById(id);
		return "redirect:/admin";
	}

}
