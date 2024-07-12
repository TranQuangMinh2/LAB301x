package com.trnqngmnh.library;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
	private CategoryService categoryService;
	@Autowired
	private CaptchaService captchaService;
	@Autowired
	private PasswordEncoder passwordEncoder;

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

		List<Category> categories = categoryService.findAllCategorys();
		model.addAttribute("latest_products", latestProduct);
		model.addAttribute("newProducts", topLatestProducts);
		model.addAttribute("products", products);
		model.addAttribute("categories", categories);
		return "shopper/index";
	}

	@GetMapping("/register-user")
	public String registerUserForm(Model model) {
		User user = new User();
		model.addAttribute("user", user);

		return "shopper/register";
	}

	/*
	 * @PostMapping("/register-user") public String addUser(@ModelAttribute User
	 * user, @RequestParam("g-recaptcha-response") String recaptchaResponse, Model
	 * model) { User existingUser = userService.findUserByEmail(user.getEmail());
	 * boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
	 * if (existingUser != null) { model.addAttribute("message",
	 * "Email đã tồn tại. Vui lòng sử dụng email khác."); return "shopper/register";
	 * } else { if (!isCaptchaValid) { userService.saveUser(user);
	 * emailService.sendRegistrationConfirmationEmail(user); return "shopper/index";
	 * } else { model.addAttribute("message",
	 * "CAPTCHA không đúng, vui lòng thử lại."); return "shopper/register"; }
	 * 
	 * } }
	 */
	@PostMapping("/register-user")
	public String addUser(@ModelAttribute User user, @RequestParam("g-recaptcha-response") String recaptchaResponse,
			Model model) {
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
				return "shopper/index";
			}
		}
	}

	@GetMapping("/customer/login")
	public String login(Model model) {
		model.addAttribute("isLoggedIn", false);
		return "shopper/login";
	}

//    @PostMapping("/customer/login")
//    public String login(@RequestParam String email, @RequestParam String password, @RequestParam("g-recaptcha-response") String recaptchaResponse, Model model, HttpSession session) {
//        User user = userService.findUserByEmail(email);
//        boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
//        if (!isCaptchaValid) {
//        	if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//        		System.out.println("Đăng nhập thành công");
//                session.setAttribute("user", user);
//    	        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
//                model.addAttribute("cartItems", cartItems);
//                model.addAttribute("cartTotalQuantity", cartItems.size());
//                model.addAttribute("isLoggedIn", true);
//                return "redirect:/index";
//            } else {
//                model.addAttribute("message", "Email hoặc mật khẩu không đúng.");
//                System.out.println("Đăng nhập thất bại");
//                return "shopper/login";
//            }
//        } else {
//            model.addAttribute("message", "CAPTCHA không đúng, vui lòng thử lại.");
//            return "shopper/login";
//        }
//        
//    }
	@PostMapping("/customer/login")
	public String login(@RequestParam String email, @RequestParam String password,
			@RequestParam("g-recaptcha-response") String recaptchaResponse, Model model, HttpSession session) {
		System.out.println("recaptchaResponse: " + recaptchaResponse);
		boolean isCaptchaValid = captchaService.verifyRecaptcha(recaptchaResponse);
		System.out.println("isCaptchaValid: " + isCaptchaValid);

		if (isCaptchaValid) {
			User user = userService.findUserByEmail(email);
			System.out.println("User: " + user);

			if (user != null && passwordEncoder.matches(password, user.getPassword())) {
				System.out.println("Đăng nhập thành công");
				session.setAttribute("user", user);
				List<CartItem> cartItems = cartItemService.getCartItemsByUserId(user.getId());
				model.addAttribute("cartItems", cartItems);
				model.addAttribute("cartTotalQuantity", cartItems.size());
				model.addAttribute("isLoggedIn", true);
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
		return "shopper/account";
	}

	@GetMapping("/my-account/edit/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		User user = userService.findUserById(id);
		model.addAttribute("user", user);
		return "shopper/account_address_fields";
	}

	@PostMapping("/my-account/edit/{id}")
	public String updateUser(@PathVariable("id") long id, User user, Model model) {
		userService.updateUser(user);
		model.addAttribute("user", user);
		model.addAttribute("address", user.getAddress());
		return "redirect:/index";
	}

	@GetMapping("/shopper/logout")
	public String shopperLogout(HttpServletRequest request) {
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
			HttpServletRequest request, HttpServletResponse response) {
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
		model.addAttribute("orderWeb", order);
		Date updatedAtDate = order.getUpdatedAt(); // Replace with your method to get the date
		LocalDateTime updatedAt = LocalDateTime.ofInstant(updatedAtDate.toInstant(), ZoneId.systemDefault());
		LocalDateTime oneMonthLater = updatedAt.plusMonths(1);
		if ("Đã thanh toán".equals(order.getPaymentStatus())) {
			String consigneeEmail = order.getConsigneeEmail(); // Get consignee email
			String subject = "Bạn đã trả tiền thành công.";
			String text = "Bạn có 1 tháng để mượn sách. Bạn sẽ phải trả sách lúc: " + oneMonthLater + ".";
			emailService.sendSimpleMessage(consigneeEmail, subject, text);
		}
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

			existingOrder.setDeliveryStatus(updatedOrder.getDeliveryStatus());
			existingOrder.setPaymentStatus(updatedOrder.getPaymentStatus());

			orderWebService.save(existingOrder);
			model.addAttribute("orderWeb", existingOrder);
			model.addAttribute("message", "Order details updated successfully");

		} else {
			model.addAttribute("message", "Order not found");
		}

		return "redirect:/order/" + orderId;
	}
}
