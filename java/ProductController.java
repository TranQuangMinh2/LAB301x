package com.trnqngmnh.library;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {

	@Autowired
	private ProductService service;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductSizeService productSizeService;
	@Autowired
	private CartItemService cartItemService;
	@Autowired
	private OrderWebRepository orderWebRepository;
	@Autowired
	private OrderWebService orderWebService;
	@Autowired
	private OrderWebDetailService orderWebDetailService;
	@Autowired
	private OrderWebDetailRepository orderWebDetailRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	ProductReviewService reviewService;
	@Autowired
	ProductSizeRepository productSizeRepository;
	@Autowired
	private BadwordService badwordService;

//	@Value("${stripe.success.url}")
//	private String successUrl;
//
//	@Value("${stripe.cancel.url}")
//	private String cancelUrl;

	@PersistenceContext
	EntityManager entityManager;

	@GetMapping("/product-listing")
	public String product_listing(HttpSession session, Model model,
			@RequestParam(defaultValue = "priceDesc") String sortBy,
			@RequestParam(value = "brandId", required = false) Long brandId,
			@RequestParam(value = "categoryId", required = false) Long categoryId) {
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		List<ProductSize> sizes = productSizeService.getAllProducts();
		model.addAttribute("sizes", sizes);
		User user = (User) session.getAttribute("user");
		boolean isLoggedIn = (user != null);
		model.addAttribute("isLoggedIn", isLoggedIn);
		Set<Long> productIdsInCart = Collections.emptySet();
		if (user != null) {
			model.addAttribute("user", user);
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("cartTotalQuantity", cartItems.size());
		}
		List<Product> products;
		if (brandId != null) {
			Brand brand = brandService.findBrandById(brandId);
			if (brand != null) {
				products = service.getProductsByBrand(brand);
				model.addAttribute("brand", brand);
			} else {
				products = service.findProducts(sortBy);
			}
		} else {
			products = service.findProducts(sortBy);
		}
		if (categoryId != null) {
			Category category = categoryService.findCategoryById(categoryId);
			if (category != null) {
				products = products.stream().filter(p -> p.getCategory().getId().equals(category.getId()))
						.collect(Collectors.toList());
				model.addAttribute("category", category);
			}
		}
		model.addAttribute("productIdsInCart", productIdsInCart);
		model.addAttribute("products", products);
		model.addAttribute("currentSort", sortBy);
		return findPaginated(1, model, session, brandId, categoryId, sortBy);
	}

	@GetMapping("/product-listing/page/{pageNo}")
	public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, HttpSession session,
			@RequestParam(value = "brandId", required = false) Long brandId,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(defaultValue = "priceDesc") String sortBy) {
		// Phân trang và lọc
		int pageSize = 12;
		Page<Product> page = service.findPaginated(pageNo, pageSize, brandId, categoryId, sortBy);
		List<Product> listProducts = page.getContent();
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("firstPage", 1);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listProducts", listProducts);

		// Lọc
		List<Category> categories = categoryService.findAllCategorys();
		model.addAttribute("categorys", categories);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		User user = (User) session.getAttribute("user");
		boolean isLoggedIn = (user != null);
		model.addAttribute("isLoggedIn", isLoggedIn);
		if (user != null) {
			model.addAttribute("user", user);
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("cartTotalQuantity", cartItems.size());
		}
		List<Product> products;
		if (brandId != null) {
			Brand brand = brandService.findBrandById(brandId);
			if (brand != null) {
				products = service.getProductsByBrand(brand);
				model.addAttribute("brand", brand);
			} else {
				products = service.findProducts(sortBy);
			}
		} else {
			products = service.findProducts(sortBy);
		}
		if (categoryId != null) {
			Category category = categoryService.findCategoryById(categoryId);
			if (category != null) {
				products = products.stream().filter(p -> p.getCategory().getId().equals(category.getId()))
						.collect(Collectors.toList());
				model.addAttribute("category", category);
			}
		}
		model.addAttribute("products", products);

		return "shopper/product-listing";
	}

	@GetMapping("/search")
	public String productListing(Model model, @RequestParam(defaultValue = "", name = "q") String text,
			@RequestParam(defaultValue = "1", name = "pageNo") int pageNo, HttpSession session,
			@RequestParam(value = "brandId", required = false) Long brandId,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(defaultValue = "priceDesc") String sortBy) {
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
		List<Product> products = service.searchProducts(text, brandId, categoryId);
		if (brandId != null) {
			Brand brand = brandService.findBrandById(brandId);
			if (brand != null) {
				products = service.getProductsByBrand(brand);
				model.addAttribute("brand", brand);
			} else {
				products = service.findProducts(sortBy);
			}
		} else {
			products = service.findProducts(sortBy);
		}
		if (categoryId != null) {
			Category category = categoryService.findCategoryById(categoryId);
			if (category != null) {
				products = products.stream().filter(p -> p.getCategory().getId().equals(category.getId()))
						.collect(Collectors.toList());
				model.addAttribute("category", category);
			}
		}
		model.addAttribute("products", products);
		model.addAttribute("text", text);
		return searchPaginated(1, model, session, text, brandId, categoryId, sortBy);
	}

	@GetMapping("/search/page/{pageNo}")
	public String searchPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, HttpSession session,
			@RequestParam(defaultValue = "", name = "q") String text,
			@RequestParam(value = "brandId", required = false) Long brandId,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(defaultValue = "priceDesc") String sortBy) {
		// Phân trang và lọc
		User user = (User) session.getAttribute("user");
		boolean isLoggedIn = (user != null);
		model.addAttribute("isLoggedIn", isLoggedIn);
		if (user != null) {
			model.addAttribute("user", user);
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("cartTotalQuantity", cartItems.size());
		}
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		int pageSize = 12;
		List<Product> products = service.getAllProducts();
		Page<Product> page = service.findPaginatedByName(pageNo, pageSize, text, brandId, categoryId);
		List<Product> listProducts = page.getContent();
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("pagedProducts", listProducts);
		model.addAttribute("products", products);
		model.addAttribute("text", text);
		if (brandId != null) {
			Brand brand = brandService.findBrandById(brandId);
			if (brand != null) {
				products = service.getProductsByBrand(brand);
				model.addAttribute("brand", brand);
			} else {
				products = service.findProducts(sortBy);
			}
		} else {
			products = service.findProducts(sortBy);
		}
		if (categoryId != null) {
			Category category = categoryService.findCategoryById(categoryId);
			if (category != null) {
				products = products.stream().filter(p -> p.getCategory().getId().equals(category.getId()))
						.collect(Collectors.toList());
				model.addAttribute("category", category);
			}
		}
		return "shopper/search";
	}

	@GetMapping("/about")
	public String about(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		boolean isLoggedIn = (user != null);
		model.addAttribute("isLoggedIn", isLoggedIn);
		if (user != null) {
			model.addAttribute("user", user);
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("cartTotalQuantity", cartItems.size());
		}
		return "shopper/about";
	}

//	@GetMapping("/contact")
//	public String contact(HttpSession session, Model model) {
//		User user = (User) session.getAttribute("user");
//		if (user != null) {
//			model.addAttribute("user", user);
//		}
//		return "shopper/contact";
//	}
//	@GetMapping("/contact")
//	public String showContactForm(Model model) {
//		model.addAttribute("contact", new Contact());
//		return "shopper/contact";
//	}
//
//	@PostMapping("/contact")
//	public String submitContactForm(@Valid Contact contact, BindingResult result, Model model) {
//		if (result.hasErrors()) {
//			return "shopper/contact";
//		}
//		contactService.saveContact(contact);
//
//		// Send email notification
//		String subject = "New Contact Form Submission: " + contact.getSubject();
//		String message = "Name: " + contact.getName() + "\n" + "Email: " + contact.getEmail() + "\n" + "Message: "
//				+ contact.getMessage();
//
//		emailService.sendSimpleMessage(contact.getEmail(), subject, message);
//
//		model.addAttribute("successMessage", "Your message has been sent successfully!");
//		return "shopper/contact";
//	}

	@GetMapping("/my-account/wishlist")
	public String wishlistt(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			model.addAttribute("user", user);
		}
		return "shopper/wishlist";
	}

	@GetMapping("/order-detail")
	public String order_detail() {
		return "admin/order-detail";
	}

//    @GetMapping("/orders")
//    public String orders(@RequestParam(required = false) String paymentMethod, Model model) {
//        List<OrderWeb> orderWebs;
//        if (paymentMethod == null || paymentMethod.isEmpty()) {
//            orderWebs = orderWebService.getAllOrders();
//        } else {
//            orderWebs = orderWebService.getOrdersByPaymentMethod(paymentMethod);
//        }
//        model.addAttribute("orderWebs", orderWebs);
//        return "admin/orders";
//    }
	@GetMapping("/orders")
	public String orders(@RequestParam(required = false) String generalSearch,
			@RequestParam(required = false) String paymentMethod, @RequestParam(required = false) String paymentStatus,
			@RequestParam(required = false) String deliveryStatus, @RequestParam(required = false) Integer totalAmount,
			Model model) {
		List<OrderWeb> orderWebs = orderWebService.getOrders(generalSearch, paymentMethod, paymentStatus,
				deliveryStatus, totalAmount);
		model.addAttribute("orderWebs", orderWebs);
		return "admin/orders";
	}

	@GetMapping("/products")
	public String getProducts(@RequestParam(defaultValue = "all", required = false) String status,
			@RequestParam(defaultValue = "", required = false) String name, Model model) {
		List<Object[]> products;
		if (!name.isEmpty()) {
			products = service.getProductsByName(name);
		} else if (!"all".equals(status)) {
			products = service.getProductsByStatus(status);
		} else {
			products = service.getProductsDetails();
		}
		model.addAttribute("products", products);
		return "admin/products";
	}

	@GetMapping("/product")
	public String getProduct(Model model) {
		List<Product> product = service.getAllProducts();
		model.addAttribute("product", product);
		return "admin/product";
	}

	@GetMapping("/addProduct")
	public String showAddProductForm(Model model) {
		Product product = new Product();
		model.addAttribute("product", product);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		return "admin/add-product";
	}

	@PostMapping("/addProduct")
	public String addProduct(@ModelAttribute Product product) {
		service.saveProduct(product);
		return "redirect:/admin";
	}

	@GetMapping("/cart")
	public String viewCart(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("user", user);
			model.addAttribute("cartTotalQuantity", cartItems.size()); // Thêm dòng này
			double totalAmount = 0;
			for (CartItem cartItem : cartItems) {
				totalAmount += cartItem.getProductSize().getProduct().getPrice() * cartItem.getQuantity();
			}
			NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
			String formattedTotalAmount = currencyFormat.format(totalAmount);
			model.addAttribute("totalAmount", formattedTotalAmount);
		}
		return "shopper/cart";
	}

//	@GetMapping("/addToCart/{id}")
//	public String addToCart(@PathVariable("id") long productSizeId, HttpSession session, Model model,
//			RedirectAttributes redirectAttributes) {
//		User user = (User) session.getAttribute("user");
//		if (user == null) {
//			redirectAttributes.addFlashAttribute("message", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng.");
//		} else {
//			System.out.println("User ID: " + user.getId());
//			System.out.println("Product Size ID: " + productSizeId);
//			cartItemService.addCartItem(productSizeId, user.getId());
//			// Sửa logic lấy CartItems
//			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
//			model.addAttribute("cartItems", cartItems);
//			model.addAttribute("user", user);
//			model.addAttribute("cartTotalQuantity", cartItems.size());
//		}
//		return "redirect:/product-listing";
//	}
	@GetMapping("/addToCart/{id}")
	public String addToCart(@PathVariable("id") long productSizeId, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			session.setAttribute("message", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng.");
		} else {
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			boolean productAlreadyInCart = cartItems.stream()
					.anyMatch(cartItem -> cartItem.getProductSize().getId() == productSizeId);

			if (productAlreadyInCart) {
				session.setAttribute("message1", "Sản phẩm đã có trong giỏ hàng.");
				session.setAttribute("productIdInCart", productSizeId);
			} else {
				cartItemService.addCartItem(productSizeId, user.getId());
				cartItems = cartItemService.getCartItemsByUserId(user.getId());
			}

			model.addAttribute("cartItems", cartItems);
			model.addAttribute("user", user);
			model.addAttribute("cartTotalQuantity", cartItems.size());
		}
		return "redirect:/product-listing";
	}

	@PostMapping("/remove-cart/{id}")
	public String removeCartItem(@PathVariable("id") Long id, HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			model.addAttribute("user", user);
		}
		cartItemService.removeCartItem(id);
		return "redirect:/cart";
	}

	@PostMapping("/delete-cart")
	public String deleteCart(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			model.addAttribute("user", user);
		}
		cartItemService.deleteAllCartItems();
		return "redirect:/cart";
	}

//	@PostMapping("/addReview")
//	public String addReview(@RequestParam("title") String title, @RequestParam("content") String content,
//			@RequestParam(value = "rating") short rating, @RequestParam("productId") long productId, Model model,
//			HttpSession session) {
//
//		Product product = service.getProductById(productId);
//		if (product == null) {
//			System.out.println("Product not found for ID: " + productId);
//			return "redirect:/product/" + productId; // hoặc xử lý lỗi theo cách khác
//		}
//
//		User user = (User) session.getAttribute("user");
//		boolean isLoggedIn = (user != null);
//		model.addAttribute("isLoggedIn", isLoggedIn);
//		if (user != null) {
//			model.addAttribute("user", user);
//			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
//			model.addAttribute("cartItems", cartItems);
//			model.addAttribute("cartTotalQuantity", cartItems.size());
//		}
//		if (user == null) {
//			System.out.println("User not found in session. Redirecting to login.");
//			return "shopper/login";
//		}
//
//		System.out.println("Product ID: " + product.getId());
//		System.out.println("User ID: " + user.getId());
//
//		ProductReview review = new ProductReview();
//		review.setTitle(title);
//		review.setContent(content);
//		review.setRating(rating);
//		review.setCreatedAt(LocalDateTime.now());
//		review.setUpdatedAt(LocalDateTime.now());
//
//		review.setProduct(product);
//		review.setUser(user);
//		reviewService.saveReview(review);
//
//		return "redirect:/product/" + productId;
//	}
	@PostMapping("/addReview")
	public String addReview(@RequestParam("title") String title, @RequestParam("content") String content,
			@RequestParam(value = "rating") short rating, @RequestParam("productId") long productId, Model model,
			HttpSession session) {

		Product product = service.getProductById(productId);
		if (product == null) {
			System.out.println("Product not found for ID: " + productId);
			return "redirect:/product/" + productId; // hoặc xử lý lỗi theo cách khác
		}

		User user = (User) session.getAttribute("user");
		boolean isLoggedIn = (user != null);
		model.addAttribute("isLoggedIn", isLoggedIn);
		if (user != null) {
			model.addAttribute("user", user);
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("cartTotalQuantity", cartItems.size());
		}
		if (user == null) {
			System.out.println("User not found in session. Redirecting to login.");
			return "shopper/login";
		}

		// Danh sách từ nhạy cảm
//		List<String> sensitiveWords = badwordService.findAllBadwords();
		List<String> sensitiveWords = badwordService.getSensitiveWords();

		model.addAttribute("sensitiveWords", sensitiveWords);

		// Phương thức lọc từ nhạy cảm
		title = filterSensitiveWords(title, sensitiveWords);
		content = filterSensitiveWords(content, sensitiveWords);

		System.out.println("Product ID: " + product.getId());
		System.out.println("User ID: " + user.getId());

		ProductReview review = new ProductReview();
		review.setTitle(title);
		review.setContent(content);
		review.setRating(rating);
		review.setCreatedAt(LocalDateTime.now());
		review.setUpdatedAt(LocalDateTime.now());

		review.setProduct(product);
		review.setUser(user);
		reviewService.saveReview(review);

		return "redirect:/product/" + productId;
	}

	private String filterSensitiveWords(String text, List<String> sensitiveWords) {
		for (String word : sensitiveWords) {
			text = text.replaceAll("(?i)\\b" + Pattern.quote(word) + "\\b", "[censored]");
		}
		return text;
	}

	@GetMapping("/product/{id}")
	public String findProductById(@PathVariable Long id, Model model, HttpSession session) {
		String sql = "SELECT p.id, p.name, p.description, p.price, p.status, p.version_name, pc.color_id, pi.path AS image_path, ps.size, ps.quantity "
				+ "FROM product p " + "LEFT JOIN product_color pc ON p.id = pc.product_id "
				+ "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_primary = 1 "
				+ "LEFT JOIN product_size ps ON p.id = ps.product_id " + "WHERE p.id = :id";

		@SuppressWarnings("unchecked")
		List<Object[]> productDetails = entityManager.createNativeQuery(sql).setParameter("id", id).getResultList();

		Product product = service.getProductById(id);
		model.addAttribute("product", product);

		// Tính toán rating trung bình
		String avgRatingSql = "SELECT AVG(rating) FROM product_review WHERE product_id = :id";
		BigDecimal averageRatingDecimal = (BigDecimal) entityManager.createNativeQuery(avgRatingSql)
				.setParameter("id", id).getSingleResult();

		Double averageRating = averageRatingDecimal != null ? averageRatingDecimal.doubleValue() : null;
		int fullStars = (averageRating != null) ? (int) Math.floor(averageRating) : 0;
		boolean hasHalfStar = (averageRating != null && (averageRating - fullStars) >= 0.5);
		int totalStars = 5; // Tổng số ngôi sao

		model.addAttribute("averageRating", averageRating);
		model.addAttribute("fullStars", fullStars);
		model.addAttribute("hasHalfStar", hasHalfStar);
		model.addAttribute("totalStars", totalStars);

		User user = (User) session.getAttribute("user");
		boolean isLoggedIn = (user != null);
		model.addAttribute("isLoggedIn", isLoggedIn);
		if (user != null) {
			model.addAttribute("user", user);
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("cartTotalQuantity", cartItems.size());
		}
		// Get products with the same category
		List<Product> relatedProducts = service.getProductsByCategory(product.getCategory());
		List<Product> relatedProducts2 = service.getProductsByBrand(product.getBrand());
		model.addAttribute("relatedProducts", relatedProducts);
		model.addAttribute("relatedProducts2", relatedProducts2);
		model.addAttribute("product", productDetails.get(0));
		model.addAttribute("productReview", product.getProductReviews());
		return "shopper/product-03";
	}

	@GetMapping("/update/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		Product product = service.getProductById(id);
		model.addAttribute("product", product);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		return "admin/update-product";
	}

	@PostMapping("/update/{id}")
	public String updateProduct(@PathVariable("id") long id, Product product, Model model) {
		service.updateProduct(product);
		model.addAttribute("product", product);
		return "redirect:/admin";
	}

	@PostMapping("/checkout")
	public String checkout(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			double totalAmount = 0;
			for (CartItem cartItem : cartItems) {
				ProductSize productSize = cartItem.getProductSize();
				double itemPrice = productSize.getProduct().getPrice();
				totalAmount += itemPrice * cartItem.getQuantity();

				// Giảm số lượng sản phẩm đi 1 đơn vị
				int newQuantity = productSize.getQuantity() - cartItem.getQuantity();
				if (newQuantity >= 0) {
					productSize.setQuantity(newQuantity);
					productSizeRepository.save(productSize); // Lưu thay đổi vào cơ sở dữ liệu
				} else {
					// Xử lý khi số lượng không đủ (nếu cần)
					// Có thể ném lỗi hoặc thông báo cho người dùng
					throw new RuntimeException("Số lượng sản phẩm không đủ để đặt hàng");
				}
			}

			// Tạo đơn hàng mới
			OrderWeb order = new OrderWeb();
			order.setUserId(user.getId());
			order.setTotalAmount((long) totalAmount);
			// Set các trường khác của order tại đây
			order.setConsignee(user.getName());
			order.setConsigneePhone(user.getPhone());
			order.setConsigneeEmail(user.getEmail());
			order.setDeliveryAddress(user.getAddress());
			order.setPaymentMethod("COD");
			order.setDeliveryStatus("Đã mượn");
			order.setPaymentStatus("Chưa thanh toán");
			orderWebRepository.save(order);

			// Tạo chi tiết đơn hàng
			for (CartItem cartItem : cartItems) {
				OrderWebDetail orderDetail = new OrderWebDetail();
				orderDetail.setOrderWeb(order);
				orderDetail.setProductSize(cartItem.getProductSize());
				orderDetail.setPrice(cartItem.getProductSize().getProduct().getPrice());
				orderDetail.setQuantity(cartItem.getQuantity());
				orderDetail.setTotalAmount(
						(long) (cartItem.getProductSize().getProduct().getPrice() * cartItem.getQuantity()));
				orderWebDetailRepository.save(orderDetail);
			}

			// Xóa tất cả các cart items
			cartItemService.deleteAllCartItems();
		}
		return "admin/order-success";
	}

	/*
	 * @PostMapping("/checkout") public String checkout(HttpSession session) { User
	 * user = (User) session.getAttribute("user"); if (user != null) {
	 * List<CartItem> cartItems =
	 * cartItemService.getCartItemsByUserId(user.getId()); double totalAmount = 0;
	 * for (CartItem cartItem : cartItems) { totalAmount +=
	 * cartItem.getProductSize().getProduct().getPrice() * cartItem.getQuantity(); }
	 * OrderWeb order = new OrderWeb(); order.setUserId(user.getId());
	 * order.setTotalAmount((long) totalAmount); // Set các trường khác của order
	 * tại đây order.setConsignee(user.getName());
	 * order.setConsigneePhone(user.getPhone());
	 * order.setConsigneeEmail(user.getEmail());
	 * order.setDeliveryAddress(user.getAddress()); order.setPaymentMethod("COD");
	 * order.setDeliveryStatus("Đã mượn");
	 * order.setPaymentStatus("Chưa thanh toán"); orderWebRepository.save(order);
	 * for (CartItem cartItem : cartItems) { OrderWebDetail orderDetail = new
	 * OrderWebDetail(); orderDetail.setOrderWeb(order);
	 * orderDetail.setProductSize(cartItem.getProductSize());
	 * orderDetail.setPrice(cartItem.getProductSize().getProduct().getPrice());
	 * orderDetail.setQuantity(cartItem.getQuantity()); orderDetail.setTotalAmount(
	 * (long) (cartItem.getProductSize().getProduct().getPrice() *
	 * cartItem.getQuantity())); orderWebDetailRepository.save(orderDetail); }
	 * cartItemService.deleteAllCartItems(); } return "admin/order-success"; }
	 */
//
//	@PostMapping("/checkoutCK")
//	public String checkoutCK(HttpSession session) {
//		User user = (User) session.getAttribute("user");
//		if (user != null) {
//			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
//			double totalAmount = 0;
//			for (CartItem cartItem : cartItems) {
//				totalAmount += cartItem.getProductSize().getProduct().getPrice() * cartItem.getQuantity();
//			}
//			OrderWeb order = new OrderWeb();
//			order.setUserId(user.getId());
//			order.setTotalAmount((long) totalAmount);
//			// Set các trường khác của order tại đây
//			order.setConsignee(user.getName());
//			order.setConsigneePhone(user.getPhone());
//			order.setConsigneeEmail(user.getEmail());
//			order.setDeliveryAddress(user.getAddress());
//			order.setPaymentMethod("ATM");
//			order.setDeliveryStatus("Chưa xét duyệt");
//			order.setPaymentStatus("Đã thanh toán");
//			orderWebRepository.save(order);
//			for (CartItem cartItem : cartItems) {
//				OrderWebDetail orderDetail = new OrderWebDetail();
//				orderDetail.setOrderWeb(order);
//				orderDetail.setProductSize(cartItem.getProductSize());
//				orderDetail.setPrice(cartItem.getProductSize().getProduct().getPrice());
//				orderDetail.setQuantity(cartItem.getQuantity());
//				orderDetail.setTotalAmount(
//						(long) (cartItem.getProductSize().getProduct().getPrice() * cartItem.getQuantity()));
//				orderWebDetailRepository.save(orderDetail);
//			}
//			cartItemService.deleteAllCartItems();
//		}
//		return "admin/order-success";
//	}
//	@PostMapping("/checkout")
//	public ModelAndView checkout(HttpSession session, @RequestParam("paymentMethodId") String paymentMethodId,
//			@RequestParam("amount") long amount) throws StripeException {
//
//		User user = (User) session.getAttribute("user");
//		if (user != null) {
//			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
//			double totalAmount = 0;
//			for (CartItem cartItem : cartItems) {
//				totalAmount += cartItem.getProductSize().getProduct().getPrice() * cartItem.getQuantity();
//			}
//
//			// Create a PaymentIntent with Stripe
//			PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount((long) (totalAmount * 100)) // Amount
//																															// in
//																															// cents
//					.setCurrency("usd") // Use the correct currency
//					.setPaymentMethod(paymentMethodId)
//					.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL).setConfirm(true)
//					.build();
//
//			PaymentIntent intent = PaymentIntent.create(params);
//
//			if ("requires_action".equals(intent.getStatus())) {
//				// Redirect the user to complete the payment
//				return new ModelAndView("redirect:" + intent.getNextAction().getRedirectToUrl());
//			}
//
//			// Handle successful payment
//			OrderWeb order = new OrderWeb();
//			order.setUserId(user.getId());
//			order.setTotalAmount((long) totalAmount);
//			// Set other fields of order here
//			order.setConsignee(user.getName());
//			order.setConsigneePhone(user.getPhone());
//			order.setConsigneeEmail(user.getEmail());
//			order.setDeliveryAddress(user.getAddress());
//			order.setPaymentMethod("Credit Card");
//			order.setDeliveryStatus("Chưa xét duyệt");
//			order.setPaymentStatus("Đã thanh toán");
//			orderWebRepository.save(order);
//
//			for (CartItem cartItem : cartItems) {
//				OrderWebDetail orderDetail = new OrderWebDetail();
//				orderDetail.setOrderWeb(order);
//				orderDetail.setProductSize(cartItem.getProductSize());
//				orderDetail.setPrice(cartItem.getProductSize().getProduct().getPrice());
//				orderDetail.setQuantity(cartItem.getQuantity());
//				orderDetail.setTotalAmount(
//						(long) (cartItem.getProductSize().getProduct().getPrice() * cartItem.getQuantity()));
//				orderWebDetailRepository.save(orderDetail);
//			}
//
//			cartItemService.deleteAllCartItems();
//
//			return new ModelAndView("admin/order-success");
//		}
//
//		return new ModelAndView("redirect:/checkout-error");
//	}

//	@PostMapping("/create-payment-intent")
//	public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> payload) {
//		try {
//			long amount = Long.parseLong(payload.get("amount").toString());
//			String paymentMethodId = (String) payload.get("paymentMethodId");
//
//			// Create PaymentIntent with Stripe API
//			PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount(amount).setCurrency("usd")
//					.setPaymentMethod(paymentMethodId)
//					.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL).setConfirm(true)
//					.build();
//
//			PaymentIntent paymentIntent = PaymentIntent.create(params);
//			return ResponseEntity.ok(Collections.singletonMap("clientSecret", paymentIntent.getClientSecret()));
//		} catch (StripeException e) {
//			// Handle error from Stripe API
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//					.body(Collections.singletonMap("error", e.getMessage()));
//		} catch (Exception e) {
//			// Handle general errors
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body(Collections.singletonMap("error", "An error occurred"));
//		}
//	}

	@GetMapping("/cancelOrder/{id}")
	public String cancelOrder(Model model, HttpSession session, @PathVariable("id") Long orderId,
			@ModelAttribute OrderWeb orderWeb) {
		// Lấy thông tin người dùng từ session
		User user = (User) session.getAttribute("user");
		if (user != null) {
			model.addAttribute("user", user);
		}

		// Tìm đơn hàng và cập nhật trạng thái
		orderWeb = orderWebService.findById(orderId);
		if (orderWeb != null) {
			orderWeb.setPaymentStatus("Đã hủy bỏ");
			orderWeb.setDeliveryStatus("Đã hủy");

			// Lấy danh sách các chi tiết đơn hàng
			List<OrderWebDetail> orderDetails = orderWebDetailRepository.findByOrderWebId(orderId);
			for (OrderWebDetail detail : orderDetails) {
				ProductSize productSize = detail.getProductSize();
				productSize.setQuantity(productSize.getQuantity() + detail.getQuantity());
				productSizeRepository.save(productSize); // Lưu lại sự thay đổi số lượng
			}

			// Lưu đơn hàng đã cập nhật
			orderWebService.save(orderWeb);

			// Lấy danh sách đơn hàng của người dùng và đưa vào model
			List<OrderWeb> orderWebs = orderWebService.getOrdersByUserId(user.getId());
			model.addAttribute("orderWebs", orderWebs);
		}

		return "redirect:/my-account";
	}
}