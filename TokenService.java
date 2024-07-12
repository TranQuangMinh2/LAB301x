package com.trnqngmnh.library;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
	@Autowired
	private PasswordResetTokenRepository tokenRepository;

	public PasswordResetToken createToken(User user) {
		String token;
		do {
			token = UUID.randomUUID().toString();
		} while (tokenRepository.findByToken(token) != null);

		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setToken(token);
		resetToken.setUser(user);
		resetToken.setExpiryDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)); // 24 hours expiration

		return tokenRepository.save(resetToken);
	}
}