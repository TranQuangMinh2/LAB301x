package com.trnqngmnh.library;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.trnqngmnh.library.entity.User;
import com.trnqngmnh.library.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

class EmailServiceTest {

	@Mock
	private JavaMailSender emailSender;

	@InjectMocks
	private EmailService emailService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSendRegistrationConfirmationEmail_Success() throws MessagingException {
		// Arrange
		User user = new User();
		user.setEmail("test@example.com");
		user.setName("Test User");

		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

		// Act
		emailService.sendRegistrationConfirmationEmail(user);

		// Assert
		verify(emailSender, times(1)).send(mimeMessage);
	}

	@Test
	void testSendRegistrationConfirmationEmail_Exception() throws MessagingException {
		// Arrange
		User user = new User();
		user.setEmail("test@example.com");
		user.setName("Test User");

		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
		doThrow(new MessagingException("Test Exception")).when(emailSender).send(mimeMessage);

		// Act
		emailService.sendRegistrationConfirmationEmail(user);

		// Assert
		verify(emailSender, times(1)).send(mimeMessage);
	}

	@Test
	void testSendSimpleMessage_Success() {
		// Arrange
		String to = "test@example.com";
		String subject = "Test Subject";
		String text = "Test Body";

		// Act
		emailService.sendSimpleMessage(to, subject, text);

		// Assert
		verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
	}

	@Test
	void testSendReminderEmail_Success() {
		// Arrange
		String toEmail = "test@example.com";
		String subject = "Reminder Subject";
		String body = "Reminder Body";

		// Act
		emailService.sendReminderEmail(toEmail, subject, body);

		// Assert
		verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
	}
}
