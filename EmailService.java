package com.trnqngmnh.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	public void sendRegistrationConfirmationEmail(User user) {
		String toEmail = user.getEmail();
		String subject = "Xác nhận đăng ký thành công";
		String body = "Chào " + user.getName() + ",\n\n"
				+ "Cảm ơn bạn đã đăng ký tài khoản trên hệ thống của chúng tôi!\n\n"
				+ "Sau khi đăng ký, bạn phải ĐĂNG NHẬP một lần nữa, vì hệ thống sẽ tự đăng xuất khi bạn thay đổi chức năng\n\n"
				+ "Vui lòng truy cập liên kết sau để kích hoạt tài khoản của bạn:\n"
				+ "https://localhost:8080/customer/login"
				+ "\n\nLưu ý: Liên kết này chỉ có hiệu lực trong vòng 24 giờ.\n\n" + "Trân trọng,\n" + "Nhóm hỗ trợ";

		MimeMessage mimeMessage = emailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom("no-reply@example.com");
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(body);
			emailSender.send(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void sendSimpleMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		emailSender.send(message);
	}

}
