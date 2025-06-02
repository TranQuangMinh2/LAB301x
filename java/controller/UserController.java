package com.trnqngmnh.library.controller;

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

import com.trnqngmnh.library.entity.Brand;
import com.trnqngmnh.library.entity.CartItem;
import com.trnqngmnh.library.entity.Category;
import com.trnqngmnh.library.entity.OrderWeb;
import com.trnqngmnh.library.entity.OrderWebDetail;
import com.trnqngmnh.library.entity.Product;
import com.trnqngmnh.library.entity.ProductSize;
import com.trnqngmnh.library.entity.User;
import com.trnqngmnh.library.repository.OrderWebDetailRepository;
import com.trnqngmnh.library.repository.ProductSizeRepository;
import com.trnqngmnh.library.service.BackupService;
import com.trnqngmnh.library.service.BrandService;
import com.trnqngmnh.library.service.CaptchaService;
import com.trnqngmnh.library.service.CartItemService;
import com.trnqngmnh.library.service.CategoryService;
import com.trnqngmnh.library.service.EmailService;
import com.trnqngmnh.library.service.OrderWebService;
import com.trnqngmnh.library.service.ProductService;
import com.trnqngmnh.library.service.UserService;

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

	@GetMapping("/orders1")
	public String orders1(@RequestParam(required = false) String generalSearch,
			@RequestParam(required = false) String paymentMethod, @RequestParam(required = false) String paymentStatus,
			@RequestParam(required = false) String deliveryStatus, @RequestParam(required = false) Integer totalAmount,
			Model model) {
		List<OrderWeb> orderWebs = orderWebService.getOrders(generalSearch, paymentMethod, paymentStatus,
				deliveryStatus, totalAmount);

		// Lấy ngày hiện tại
		Date currentDate = new Date();

		// Kiểm tra và cập nhật trạng thái thanh toán
		for (OrderWeb order : orderWebs) {
			if (order.getUpdatedAt() != null && currentDate.after(order.getUpdatedAt())
					&& !"Đã trả".equals(order.getDeliveryStatus())) {
				order.setPaymentStatus("Quá hạn");
				// Cập nhật đơn hàng trong cơ sở dữ liệu nếu cần thiết
				orderWebService.save(order);
			} else {
				order.setPaymentStatus("Trong hạn");
				orderWebService.save(order);
			}
		}

		model.addAttribute("orderWebs", orderWebs);
		return "admin/orders2";
	}

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

//	@PostMapping("/my-account/edit/{id}")
//	public String updateUser(@PathVariable("id") long id, User user, Model model) {
//		userService.updateUser(user);
//		model.addAttribute("user", user);
//		model.addAttribute("address", user.getAddress());
//		List<Category> categorys = categoryService.findAllCategorys();
//		model.addAttribute("categorys", categorys);
//		List<Brand> brands = brandService.findAllBrands();
//		model.addAttribute("brands", brands);
//		return "redirect:/index";
//	}
	@PostMapping("/my-account/edit/{id}")
	public String updateUser(@PathVariable("id") long id, @RequestParam("old_password") String oldPassword,
			@RequestParam("password") String newPassword, @RequestParam("confirm_password") String confirmPassword,
			User user, Model model) {
		// Lấy user từ cơ sở dữ liệu
		User currentUser = userService.findUserById(id);

		// Kiểm tra mật khẩu cũ
		if (!userService.checkPassword(oldPassword, currentUser.getPassword())) {
			model.addAttribute("error", "Mật khẩu cũ không đúng.");
			return "redirect:/my-account/edit/" + id;
		}

		// Kiểm tra xác nhận mật khẩu mới
		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "Mật khẩu mới không khớp.");
			return "redirect:/my-account/edit/" + id;
		}

		// Cập nhật mật khẩu mới cho user
		// Mã hóa mật khẩu mới
		String encodedPassword = passwordEncoder.encode(newPassword);

		// Cập nhật mật khẩu mới cho user
		currentUser.setPassword(encodedPassword);
		userService.updateUser(currentUser);

		model.addAttribute("user", currentUser);
		model.addAttribute("address", currentUser.getAddress());
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

	private BackupService backupService = new BackupService();
	private static boolean isBackupScheduled = false;

	@GetMapping("/customer/checkLogin")
	public String admin_login() {
		// Start scheduling backup if not already started
		if (!isBackupScheduled) {
			String[] tablesToBackup = { "brand", "category", "badword" }; // Add your tables here
			backupService.scheduleAutoBackup("root", "123456", "library_manage", tablesToBackup);
			isBackupScheduled = true; // Prevent re-scheduling
		}
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
		User existingUser = userService.findUserByEmail(user.getEmail());
		if (existingUser != null) {
			model.addAttribute("message", "Email đã tồn tại. Vui lòng sử dụng email khác.");
			return "admin/customer";
		} else {
			String encodedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);
			userService.saveUser(user);
			List<User> users = userService.getAllUsers();
			model.addAttribute("users", users);
			return "redirect:/customers";
		}
	}

	@GetMapping("/order/{orderId}")
	public String viewOrderDetails(@PathVariable("orderId") Long orderId, Model model) {
		OrderWeb order = orderWebService.findById(orderId);
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

	// Khi người dùng nhấn "YES"
	@PostMapping("/confirm-extension-yes/{id}")
	public String confirmExtensionYes(@PathVariable("id") Long orderId, Model model) {
		OrderWeb order = orderWebService.findById(orderId);

		if (order != null && order.getPaymentMethod().contains("Chờ gia hạn")) {
			// Cộng thêm daysToAdd vào ngày có sẵn trước khi gán lại paymentMethod
			int daysToAdd = extractDaysToAdd(order.getPaymentMethod());

			// Cộng thêm số ngày gia hạn vào updatedAt
			Calendar cal = Calendar.getInstance();
			cal.setTime(order.getUpdatedAt());
			cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
			order.setUpdatedAt(cal.getTime());

			// Gán lại paymentMethod sau khi đã xử lý ngày
			order.setPaymentMethod("Đồng ý gia hạn");
			order.setSentMail2(null);
			// Lưu thay đổi
			orderWebService.save(order);
		}

		return "redirect:/orders"; // Điều hướng lại về trang danh sách đơn hàng
	}

	// Khi người dùng nhấn "NO"
	@PostMapping("/confirm-extension-no/{id}")
	public String confirmExtensionNo(@PathVariable("id") Long orderId, Model model) {
		OrderWeb order = orderWebService.findById(orderId);

		if (order != null && order.getPaymentMethod().contains("Chờ gia hạn")) {
			// Gán lại paymentMethod
			order.setPaymentMethod("Không gia hạn");

			// Không thay đổi ngày updatedAt
			orderWebService.save(order);
		}

		return "redirect:/orders"; // Điều hướng lại về trang danh sách đơn hàng
	}

	// Hàm để lấy số ngày gia hạn từ paymentMethod
	private int extractDaysToAdd(String paymentMethod) {
		// Ví dụ: Chờ gia hạn 5 ngày => tách lấy số 5
		String[] parts = paymentMethod.split(" ");
		for (String part : parts) {
			try {
				return Integer.parseInt(part); // Tìm và trả về số daysToAdd
			} catch (NumberFormatException ignored) {
				// Nếu không phải là số, tiếp tục
			}
		}
		return 0; // Trường hợp không tìm thấy số, trả về 0
	}

//	@PostMapping("/extend-order/{orderId}")
//	public String extendOrder(@PathVariable("orderId") Long orderId, Model model) {
//		OrderWeb order = orderWebService.findById(orderId);
//		if (order != null) {
//			// Cộng thêm 1 ngày vào updatedAt
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(order.getUpdatedAt());
//			cal.add(Calendar.DAY_OF_MONTH, 1);
//			order.setUpdatedAt(cal.getTime());
//			// Cập nhật paymentStatus thành "Trong hạn"
//			order.setPaymentStatus("Trong hạn");
//			// Lưu lại order vào cơ sở dữ liệu
//			orderWebService.save(order);
//		}
//		model.addAttribute("orderWeb", order);
//		List<Category> categorys = categoryService.findAllCategorys();
//		model.addAttribute("categorys", categorys);
//		List<Brand> brands = brandService.findAllBrands();
//		model.addAttribute("brands", brands);
//		// Điều hướng trở lại trang đơn hàng sau khi gia hạn
//		return "redirect:/my-order";
//	}
	@PostMapping("/extend-order/{orderId}")
	public String extendOrder(@PathVariable("orderId") Long orderId, @RequestParam("daysToAdd") int daysToAdd,
			Model model) {
		OrderWeb order = orderWebService.findById(orderId);
		if (order != null) {
			// Cộng thêm số ngày vào updatedAt dựa trên input
			Calendar cal = Calendar.getInstance();
			cal.setTime(order.getUpdatedAt());
//			cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
			order.setUpdatedAt(cal.getTime());
			// Cập nhật paymentStatus thành "Trong hạn"
			order.setPaymentStatus("Trong hạn");
			order.setPaymentMethod("Chờ gia hạn " + daysToAdd + " ngày");
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
