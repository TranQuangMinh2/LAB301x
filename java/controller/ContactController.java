package com.trnqngmnh.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.trnqngmnh.library.entity.Brand;
import com.trnqngmnh.library.entity.CartItem;
import com.trnqngmnh.library.entity.Category;
import com.trnqngmnh.library.entity.ContactForm;
import com.trnqngmnh.library.entity.User;
import com.trnqngmnh.library.service.BrandService;
import com.trnqngmnh.library.service.CartItemService;
import com.trnqngmnh.library.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ContactController {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private CartItemService cartItemService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/contact")
	public String showContactForm(Model model, HttpSession session) {
		model.addAttribute("contactForm", new ContactForm());
		User user = (User) session.getAttribute("user");
		boolean isLoggedIn = (user != null);
		model.addAttribute("isLoggedIn", isLoggedIn);
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		if (user != null) {
			model.addAttribute("user", user);
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("cartTotalQuantity", cartItems.size());
		}
		return "shopper/contact";
	}

	@PostMapping("/contact")
	public String submitContactForm(@ModelAttribute("contactForm") ContactForm contactForm, Model model) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(contactForm.getEmail());
			message.setTo("jacktran234@gmail.com");
			message.setSubject(contactForm.getSubject());
			message.setText(contactForm.getMessage());

			mailSender.send(message);

			model.addAttribute("successMessage", "Your message has been sent successfully!");
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Failed to send message. Please try again later.");
		}

		return "shopper/contact";
	}
}
