package com.trnqngmnh.library;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {
	@Autowired
	private UserService userService;

	@Autowired
	private PasswordResetTokenRepository tokenRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private TokenService tokenService;

	// Quản lí
	@GetMapping("/admin/forgot-password")
	public String showForgotPasswordFormAdmin() {
		return "admin/forgot-password";
	}

	@PostMapping("/admin/forgot-password")
	public String processForgotPasswordFormAdmin(@RequestParam("email") String userEmail, Model model) {
		User user = userService.findUserByEmail(userEmail);
		if (user == null) {
			model.addAttribute("errorMessage", "Không có người dùng nào được tìm thấy");
			return "admin/forgot-password";
		}

		PasswordResetToken token = tokenService.createToken(user);

		String resetUrl = "http://localhost:8080/admin/reset-password?token=" + token.getToken();
		emailService.sendSimpleMessage(userEmail, "Password Reset Request",
				"Click the link to reset your password: " + resetUrl);

		model.addAttribute("message", "Một đường link đã được gửi tới" + userEmail);
		return "admin/forgot-password";
	}

	@GetMapping("/admin/reset-password")
	public String showResetPasswordFormAdmin(@RequestParam("token") String token, Model model) {
		PasswordResetToken resetToken = tokenRepository.findByToken(token);
		if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
			model.addAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn.");
			return "admin/reset-password";
		}

		model.addAttribute("token", token);
		return "admin/reset-password";
	}

	@PostMapping("/admin/reset-password")
	public String processResetPasswordFormAdmin(@RequestParam("token") String token,
			@RequestParam("password") String password, Model model) {
		PasswordResetToken resetToken = tokenRepository.findByToken(token);
		if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
			model.addAttribute("errorMessage", "Invalid or expired token.");
			return "admin/reset-password";
		}

		User user = resetToken.getUser();
		userService.updatePassword(user, password);

		tokenRepository.delete(resetToken);

		model.addAttribute("message", "Mật khẩu đã được đặt lại thành công.");
		return "admin/login";
	}

	// Người dùng
	@GetMapping("/forgot-password")
	public String showForgotPasswordForm() {
		return "shopper/forgot-password";
	}

	@PostMapping("/forgot-password")
	public String processForgotPasswordForm(@RequestParam("email") String userEmail, Model model) {
		User user = userService.findUserByEmail(userEmail);
		if (user == null) {
			model.addAttribute("errorMessage", "Không có người dùng nào được tìm thấy có email đó.");
			return "shopper/forgot-password";
		}

		PasswordResetToken token = tokenService.createToken(user);

		String resetUrl = "http://localhost:8080/reset-password?token=" + token.getToken();
		emailService.sendSimpleMessage(userEmail, "Yêu cầu đặt lại mật khẩu",
				"Click vào đường link để đặt lại mật khẩu của bạn: " + resetUrl);

		model.addAttribute("message", "Đường link đặt lại mật khẩu đã được gửi tới " + userEmail);
		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
		PasswordResetToken resetToken = tokenRepository.findByToken(token);
		if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
			model.addAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn.");
			return "shopper/reset-password";
		}

		model.addAttribute("token", token);
		return "shopper/reset-password";
	}

	@PostMapping("/reset-password")
	public String processResetPasswordForm(@RequestParam("token") String token,
			@RequestParam("password") String password, Model model) {
		PasswordResetToken resetToken = tokenRepository.findByToken(token);
		if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
			model.addAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn.");
			return "shopper/reset-password";
		}

		User user = resetToken.getUser();
		userService.updatePassword(user, password);

		tokenRepository.delete(resetToken);

		model.addAttribute("message", "Mật khẩu đã được đặt lại thành công.");
		return "shopper/login";
	}
}