package com.trnqngmnh.library;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private CartItemService cartItemService;
	@Autowired
	private OrderWebService orderWebService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CaptchaService captchaService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private OrderWebDetailRepository orderWebDetailRepository;
	@Autowired
	ProductSizeRepository productSizeRepository;

//---------Shopper--------------------
	@GetMapping("/index")
	public String index(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			model.addAttribute("user", user);
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("cartTotalQuantity", cartItems.size());
		}
		List<Product> products = productService.getAllProducts();
		products.sort(Comparator.comparing(Product::getCreatedAt).reversed());

		Product latestProduct = products.get(0);
		List<Product> latestProduct2 = productService.getAllProducts();
		latestProduct2.sort(Comparator.comparing(Product::getCreatedAt).reversed());
		List<Product> topLatestProducts = latestProduct2.stream().limit(8).collect(Collectors.toList());
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		List<Category> categories = categoryService.findAllCategorys();
		model.addAttribute("latest_products", latestProduct);
		model.addAttribute("newProducts", topLatestProducts);
		model.addAttribute("products", products);
		model.addAttribute("categories", categories);
		return "shopper/index";
	}

//	@GetMapping("/register-user")
//	public String registerUserForm(Model model) {
//		User user = new User();
//		model.addAttribute("user", user);
//
//		return "shopper/register";
//	}
//
//	@PostMapping("/register-user")
//	public String addUser(@ModelAttribute User user, @RequestParam("g-recaptcha-response") String recaptchaResponse,
//			Model model) {
//		User existingUser = userService.findUserByEmail(user.getEmail());
//		boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
//		if (existingUser != null) {
//			model.addAttribute("message", "Email đã tồn tại. Vui lòng sử dụng email khác.");
//			return "shopper/register";
//		} else {
//			if (!isCaptchaValid) {
//				model.addAttribute("message", "CAPTCHA không đúng, vui lòng thử lại.");
//				return "shopper/register";
//			} else {
//				// Mã hóa mật khẩu trước khi lưu
//				String encodedPassword = passwordEncoder.encode(user.getPassword());
//				user.setPassword(encodedPassword);
//
//				userService.saveUser(user);
//				emailService.sendRegistrationConfirmationEmail(user);
//				if (user != null) {
//					model.addAttribute("user", user);
//					List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
//					model.addAttribute("cartItems", cartItems);
//					model.addAttribute("cartTotalQuantity", cartItems.size());
//				}
//				List<Product> products = productService.getAllProducts();
//				products.sort(Comparator.comparing(Product::getCreatedAt).reversed());
//
//				Product latestProduct = products.get(0);
//				List<Product> latestProduct2 = productService.getAllProducts();
//				latestProduct2.sort(Comparator.comparing(Product::getCreatedAt).reversed());
//				List<Product> topLatestProducts = latestProduct2.stream().limit(8).collect(Collectors.toList());
//
//				List<Category> categories = categoryService.findAllCategorys();
//				model.addAttribute("latest_products", latestProduct);
//				model.addAttribute("newProducts", topLatestProducts);
//				model.addAttribute("products", products);
//				model.addAttribute("categories", categories);
//				return "shopper/index";
//			}
//		}
//	}
	@GetMapping("/register-user")
	public String registerUser(Model model) {
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		List<Product> products = productService.getAllProducts();
		model.addAttribute("products", products);

		return "shopper/register";
	}

	@PostMapping("/register-user")
	public String addUser(@ModelAttribute User user, @RequestParam("g-recaptcha-response") String recaptchaResponse,
			Model model, HttpSession session) {
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		User existingUser = userService.findUserByEmail(user.getEmail());
		boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
		if (existingUser != null) {
			model.addAttribute("message", "Email đã tồn tại. Vui lòng sử dụng email khác.");
			return "shopper/register";
		} else {
			if (!isCaptchaValid) {
				model.addAttribute("message", "CAPTCHA không đúng, vui lòng thử lại.");
				return "shopper/register";
			} else {
				// Mã hóa mật khẩu trước khi lưu
				String encodedPassword = passwordEncoder.encode(user.getPassword());
				user.setPassword(encodedPassword);

				userService.saveUser(user);
				emailService.sendRegistrationConfirmationEmail(user);
				if (user != null) {
					model.addAttribute("user", user);
					List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
					model.addAttribute("cartItems", cartItems);
					model.addAttribute("cartTotalQuantity", cartItems.size());
				}
				List<Product> products = productService.getAllProducts();
				products.sort(Comparator.comparing(Product::getCreatedAt).reversed());

				Product latestProduct = products.get(0);
				List<Product> latestProduct2 = productService.getAllProducts();
				latestProduct2.sort(Comparator.comparing(Product::getCreatedAt).reversed());
				List<Product> topLatestProducts = latestProduct2.stream().limit(8).collect(Collectors.toList());

				List<Category> categories = categoryService.findAllCategorys();
				model.addAttribute("latest_products", latestProduct);
				model.addAttribute("newProducts", topLatestProducts);
				model.addAttribute("products", products);
				model.addAttribute("categories", categories);
				return "shopper/index";
			}
		}
	}
//	@PostMapping("/register-user")
//	public String addUser(@ModelAttribute User user, @RequestParam("g-recaptcha-response") String recaptchaResponse,
//			Model model, HttpSession session) {
//		User existingUser = userService.findUserByEmail(user.getEmail());
//		boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
//		if (existingUser != null) {
//			model.addAttribute("message", "Email đã tồn tại. Vui lòng sử dụng email khác.");
//			return "shopper/register";
//		} else {
//			if (!isCaptchaValid) {
//				model.addAttribute("message", "CAPTCHA không đúng, vui lòng thử lại.");
//				return "shopper/register";
//			} else {
//				// Mã hóa mật khẩu trước khi lưu
//				String encodedPassword = passwordEncoder.encode(user.getPassword());
//				user.setPassword(encodedPassword);
//
//				userService.saveUser(user);
//				emailService.sendRegistrationConfirmationEmail(user);
//				// Đăng nhập tự động sau khi đăng ký thành công
//				session.setAttribute("user", user);
//
//				List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
//				model.addAttribute("cartItems", cartItems);
//				model.addAttribute("cartTotalQuantity", cartItems.size());
//				List<Product> products = productService.getAllProducts();
//				products.sort(Comparator.comparing(Product::getCreatedAt).reversed());
//				Product latestProduct = products.get(0);
//				List<Product> topLatestProducts = products.stream().limit(8).collect(Collectors.toList());
//				List<Category> categories = categoryService.findAllCategorys();
//				model.addAttribute("latest_products", latestProduct);
//				model.addAttribute("newProducts", topLatestProducts);
//				model.addAttribute("products", products);
//				model.addAttribute("categories", categories);
//				return "shopper/index";
//			}
//		}
//	}

//	@GetMapping("/customer/login")
//	public String login(Model model) {
//		model.addAttribute("isLoggedIn", false);
//		return "shopper/login";
//	}
//
//	@PostMapping("/customer/login")
//	public String login(@RequestParam String email, @RequestParam String password,
//			@RequestParam("g-recaptcha-response") String recaptchaResponse, Model model, HttpSession session) {
//		User user = userService.findUserByEmail(email);
//		boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
//
//		if (isCaptchaValid) {
//			if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//				System.out.println("Đăng nhập thành công");
//				session.setAttribute("user", user);
//				List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
//				model.addAttribute("cartItems", cartItems);
//				model.addAttribute("cartTotalQuantity", cartItems.size());
//				model.addAttribute("isLoggedIn", true);
//				return "redirect:/index";
//			} else {
//				model.addAttribute("message", "Email hoặc mật khẩu không đúng.");
//				System.out.println("Đăng nhập thất bại");
//				return "shopper/login";
//			}
//		} else {
//			model.addAttribute("message", "CAPTCHA không đúng, vui lòng thử lại.");
//			return "shopper/login";
//		}
//
//	}
	@GetMapping("/customer/login")
	public String loginUser(Model model) {
		List<Product> products = productService.getAllProducts();
		model.addAttribute("products", products);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		return "shopper/login";
	}

	@PostMapping("/customer/login")
	public String login(@RequestParam String email, @RequestParam String password,
			@RequestParam("g-recaptcha-response") String recaptchaResponse, Model model, HttpSession session) {
		System.out.println("recaptchaResponse: " + recaptchaResponse);
		boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
		System.out.println("isCaptchaValid: " + isCaptchaValid);
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		if (isCaptchaValid) {
			User user = userService.findUserByEmail(email);
			System.out.println("User: " + user);

			if (user != null && passwordEncoder.matches(password, user.getPassword())) {
				System.out.println("Đăng nhập thành công");
				session.setAttribute("user", user);
				List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
				model.addAttribute("cartItems", cartItems);
				model.addAttribute("cartTotalQuantity", cartItems.size());
				model.addAttribute("isSignedIn", true);
				return "redirect:/index";
			} else {
				model.addAttribute("message", "Email hoặc mật khẩu không đúng.");
				System.out.println("Đăng nhập thất bại");
				return "shopper/login";
			}
		} else {
			model.addAttribute("message", "CAPTCHA không đúng, vui lòng thử lại.");
			System.out.println("CAPTCHA không đúng");
			return "shopper/login";
		}
	}
//	@PostMapping("/customer/login")
//	public String login(@RequestParam String email, @RequestParam String password,
//			@RequestParam("g-recaptcha-response") String recaptchaResponse, Model model, HttpSession session) {
//		boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
//		if (isCaptchaValid) {
//			User user = userService.findUserByEmail(email);
//			if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//				session.setAttribute("user", user);
//				List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
//				model.addAttribute("cartItems", cartItems);
//				model.addAttribute("cartTotalQuantity", cartItems.size());
//				model.addAttribute("isSignedIn", true);
//				return "redirect:/index";
//			} else {
//				model.addAttribute("message", "Email hoặc mật khẩu không đúng.");
//				return "shopper/login";
//			}
//		} else {
//			model.addAttribute("message", "CAPTCHA không đúng, vui lòng thử lại.");
//			return "shopper/login";
//		}
//	}

//	@PostMapping("/customer/login")
//	public String login(@RequestParam String email, @RequestParam String password,
//			@RequestParam("g-recaptcha-response") String recaptchaResponse, Model model, HttpSession session) {
//		System.out.println("recaptchaResponse: " + recaptchaResponse);
//		boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
//		System.out.println("isCaptchaValid: " + isCaptchaValid);
//
//		if (isCaptchaValid) {
//			User user = userService.findUserByEmail(email);
//			System.out.println("User: " + user);
//
//			if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//				System.out.println("Đăng nhập thành công");
//				session.setAttribute("user", user);
//				List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
//				model.addAttribute("cartItems", cartItems);
//				model.addAttribute("cartTotalQuantity", cartItems.size());
//				model.addAttribute("isLoggedIn", true);
//				return "redirect:/index";
//			} else {
//				model.addAttribute("message", "Email hoặc mật khẩu không đúng.");
//				System.out.println("Đăng nhập thất bại");
//				return "shopper/login";
//			}
//		} else {
//			model.addAttribute("message", "CAPTCHA không đúng, vui lòng thử lại.");
//			System.out.println("CAPTCHA không đúng");
//			return "shopper/login";
//		}
//	}

	@GetMapping("/my-account")
	public String myAccount(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			model.addAttribute("user", user);
			model.addAttribute("address", user.getAddress());
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("cartTotalQuantity", cartItems.size());
			// Lấy danh sách các đơn hàng của người dùng
			List<OrderWeb> orderWebs = orderWebService.getOrdersByUserId(user.getId());
			model.addAttribute("orderWebs", orderWebs);
		} else {
			return "shopper/login";
		}
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		return "shopper/account";
	}

	@GetMapping("/my-order")
	public String myOrder(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user != null) {

			model.addAttribute("user", user);
			model.addAttribute("address", user.getAddress());
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("cartTotalQuantity", cartItems.size());
			// Lấy danh sách các đơn hàng của người dùng
			List<OrderWeb> orderWebs = orderWebService.getOrdersByUserId(user.getId());
			model.addAttribute("orderWebs", orderWebs);
		} else {
			return "shopper/login";
		}
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		return "shopper/account2";
	}

	@GetMapping("/my-account/edit/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		User user = userService.findUserById(id);
		model.addAttribute("user", user);
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		model.addAttribute("user", user);
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		return "shopper/account_address_fields";
	}

	@PostMapping("/my-account/edit/{id}")
	public String updateUser(@PathVariable("id") long id, User user, Model model) {
		userService.updateUser(user);
		model.addAttribute("user", user);
		model.addAttribute("address", user.getAddress());
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		return "redirect:/index";
	}

	@GetMapping("/my-account/order/{id}")
	public String orderDetails(@PathVariable("id") Long id, HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		OrderWeb orderWeb = orderWebService.findById(id);

//		Date updatedAtDate = orderWeb.getCreatedAt();
//		LocalDateTime updatedAt = LocalDateTime.ofInstant(updatedAtDate.toInstant(), ZoneId.systemDefault());
//		LocalDateTime oneMonthLater = updatedAt.plusMonths(1);
		if (user != null) {
			// List<OrderWeb> orderWebs = orderWebService.findByUserId(user.getId());
			model.addAttribute("user", user);
			List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
			model.addAttribute("cartTotalQuantity", cartItems.size()); // Thêm dòng này
			// model.addAttribute("Product", product);
		} else {
			return "shopper/login";
		}
//		model.addAttribute("oneMonthLater", oneMonthLater); // Thêm dòng này
		model.addAttribute("orderWeb", orderWeb);
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		return "shopper/order-detail";
	}

	@GetMapping("/shopper/logout")
	public String shopperLogout(HttpServletRequest request, Model model) {
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		request.getSession().invalidate();
		return "redirect:/index";
	}

	// ---------------------admin-----------------------------
	@GetMapping("/admin")
	public String admin() {
		return "admin/admin";
	}

	@GetMapping("/customers")
	public String customers(Model model) {
		List<User> user = userService.getAllUsers();
		model.addAttribute("user", user);
		return "admin/customer";
	}

	@GetMapping("/personal-information")
	public String getUser(Model model) {
		List<User> user = userService.getAllUsers();
		model.addAttribute("user", user);
		return "admin/personal-information";
	}

	@GetMapping("/customer/checkLogin")
	public String admin_login() {
		return "admin/login";
	}

	@PostMapping("/customer/checkLogin")
	public String checkLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			Model model, @RequestParam(value = "remember", defaultValue = "false") boolean remember,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("g-recaptcha-response") String recaptchaResponse) {
		boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
		if (isCaptchaValid) {
			User user = userService.checkLogin(email, password);
			if (user != null && "admin".equals(user.getRole())) {
				request.getSession().setAttribute("loggedInUser", user);
				if (remember) {
					addRememberMeCookie(response, email);
				}
				return "redirect:/admin";
			} else {
				model.addAttribute("error", "Đăng nhập không thành công. Vui lòng kiểm tra lại email và mật khẩu.");
				return "admin/login";
			}
		} else {
			model.addAttribute("message", "CAPTCHA không đúng, vui lòng thử lại.");
			System.out.println("CAPTCHA không đúng");
			return "admin/login";
		}
	}

	private void addRememberMeCookie(HttpServletResponse response, String email) {
		Cookie cookie = new Cookie("REMEMBER_ME", email);
		cookie.setMaxAge(24 * 60 * 60); // 1 day
		response.addCookie(cookie);
	}

	@GetMapping("/customer/logout")
	public String logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "admin/login";
	}

	@GetMapping("/dashboard")
	public String dashboard() {
		return "admin/dashboard";
	}

	@GetMapping("/layout")
	public String layout() {
		return "admin/layout";
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable Long id, Model model) {
		userService.deleteUserById(id);
		model.addAttribute("user", userService.getAllUsers());
		return "admin/customer";
	}

	@GetMapping("/updateUser/{id}")
	public String showUpdateUserForm(@PathVariable("id") Long id, Model model) {
		User user = userService.findUserById(id);
		model.addAttribute("user", user);
		return "admin/update-customer";
	}

	@PostMapping("/updateUser/{id}")
	public String updateUser2(@PathVariable("id") long id, User user, Model model) {
		userService.updateUser(user);
		model.addAttribute("user", user);
		return "redirect:/admin";
	}

	@GetMapping("/addUser")
	public String addUser(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "admin/add-user";
	}

	@PostMapping("/addUser")
	public String saveUser(@ModelAttribute User user, Model model) {
		userService.saveUser(user);
		List<User> users = userService.getAllUsers();
		model.addAttribute("users", users);
		return "redirect:/customers";
	}

	@GetMapping("/order/{orderId}")
	public String viewOrderDetails(@PathVariable("orderId") Long orderId, Model model) {
		OrderWeb order = orderWebService.findById(orderId);
		if (order.getSentMail() == null || !order.getSentMail()) {
			Date updatedAtDate = order.getCreatedAt();
			LocalDateTime updatedAt = LocalDateTime.ofInstant(updatedAtDate.toInstant(), ZoneId.systemDefault());
			LocalDateTime oneMonthLater = updatedAt.plusMonths(1);
			if ("Đã mượn".equals(order.getDeliveryStatus())) {
				String consigneeEmail = order.getConsigneeEmail(); // Get consignee email
				String subject = "Bạn đã mượn sách thành công.";
				String text = "Bạn có 1 tháng để mượn sách. Bạn sẽ phải trả sách lúc: " + oneMonthLater
						+ ". Lưu ý là bạn phải đến thư viện để trả sách.";
				emailService.sendSimpleMessage(consigneeEmail, subject, text);
				order.setSentMail(true);
			}
		}
		model.addAttribute("orderWeb", order);
		return "admin/order-detail";
	}

	@GetMapping("/order/{orderId}/update")
	public String showUpdateForm(@PathVariable("orderId") Long orderId, Model model) {
		OrderWeb existingOrder = orderWebService.findById(orderId);

		if (existingOrder != null) {
			model.addAttribute("orderWeb", existingOrder);
		} else {
			model.addAttribute("message", "Order not found");
		}

		return "admin/order-update";
	}

	@PostMapping("/order/{orderId}/update")
	public String updateOrderDetails(@PathVariable("orderId") Long orderId, @ModelAttribute OrderWeb updatedOrder,
			Model model) {
		OrderWeb existingOrder = orderWebService.findById(orderId);
		if (existingOrder != null) {
			String newStatus = updatedOrder.getDeliveryStatus();
			String oldStatus = existingOrder.getDeliveryStatus();

			// Cập nhật trạng thái và thông tin khác
			existingOrder.setDeliveryStatus(newStatus);
			/* existingOrder.setPaymentStatus(updatedOrder.getPaymentStatus()); */
			orderWebService.save(existingOrder);

			// Kiểm tra nếu trạng thái mới là "Đã trả" và trạng thái cũ không phải là "Đã
			// trả"
			if ("Đã trả".equals(newStatus) && !"Đã trả".equals(oldStatus)) {
				List<OrderWebDetail> orderDetails = orderWebDetailRepository.findByOrderWeb(existingOrder);
				for (OrderWebDetail orderDetail : orderDetails) {
					ProductSize productSize = orderDetail.getProductSize();
					int newQuantity = productSize.getQuantity() + 1;

					existingOrder.setPaymentStatus("Trong hạn");

					productSize.setQuantity(newQuantity);
					productSizeRepository.save(productSize); // Lưu thay đổi vào cơ sở dữ liệu
				}
			}

			model.addAttribute("orderWeb", existingOrder);
			model.addAttribute("message", "Order details updated successfully");
		} else {
			model.addAttribute("message", "Order not found");
		}

		return "redirect:/order/" + orderId;
	}

	@PostMapping("/send-reminder-email/{id}")
	public String sendReminderEmail(@PathVariable("id") Long orderId, RedirectAttributes redirectAttributes) {
		OrderWeb order = orderWebService.findById(orderId);
		if (order != null && "Quá hạn".equals(order.getPaymentStatus())) {
			String subject = "Nhắc nhở: Trạng thái mượn sách Quá hạn";
			String body = "Xin chào " + order.getConsignee() + ",\n\n" + "Đơn mượn của bạn có mã số " + order.getId()
					+ " hiện đang trong trạng thái 'Quá hạn'. "
					+ "Vui lòng trả sách hoặc gia hạn để tránh bất kỳ sự bất tiện nào.\n\n"
					+ "Cảm ơn bạn đã sử dụng dịch vụ thư viện của chúng tôi.";

			emailService.sendReminderEmail(order.getConsigneeEmail(), subject, body);

			redirectAttributes.addFlashAttribute("message", "Email nhắc nhở đã được gửi thành công!");
		} else {
			redirectAttributes.addFlashAttribute("error",
					"Không thể gửi email nhắc nhở. Đơn hàng không tồn tại hoặc không ở trạng thái 'Quá hạn'.");
		}

		return "redirect:/orders"; // Điều hướng trở lại trang đơn hàng
	}

	@PostMapping("/extend-order/{orderId}")
	public String extendOrder(@PathVariable("orderId") Long orderId, Model model) {
		OrderWeb order = orderWebService.findById(orderId);
		if (order != null) {
			// Cộng thêm 1 ngày vào updatedAt
			Calendar cal = Calendar.getInstance();
			cal.setTime(order.getUpdatedAt());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			order.setUpdatedAt(cal.getTime());
			// Cập nhật paymentStatus thành "Trong hạn"
			order.setPaymentStatus("Trong hạn");
			// Lưu lại order vào cơ sở dữ liệu
			orderWebService.save(order);
		}
		model.addAttribute("orderWeb", order);
		List<Category> categorys = categoryService.findAllCategorys();
		model.addAttribute("categorys", categorys);
		List<Brand> brands = brandService.findAllBrands();
		model.addAttribute("brands", brands);
		// Điều hướng trở lại trang đơn hàng sau khi gia hạn
		return "redirect:/my-order";
	}

	/*
	 * @PostMapping("/order/{orderId}/update") public String
	 * updateOrderDetails(@PathVariable("orderId") Long orderId, @ModelAttribute
	 * OrderWeb updatedOrder, Model model) { OrderWeb existingOrder =
	 * orderWebService.findById(orderId); if (existingOrder != null) {
	 * 
	 * existingOrder.setDeliveryStatus(updatedOrder.getDeliveryStatus());
	 * existingOrder.setPaymentStatus(updatedOrder.getPaymentStatus());
	 * 
	 * orderWebService.save(existingOrder); model.addAttribute("orderWeb",
	 * existingOrder); model.addAttribute("message",
	 * "Order details updated successfully");
	 * 
	 * } else { model.addAttribute("message", "Order not found"); }
	 * 
	 * return "redirect:/order/" + orderId; }
	 */
}
