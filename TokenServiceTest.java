package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.trnqngmnh.library.entity.PasswordResetToken;
import com.trnqngmnh.library.entity.User;
import com.trnqngmnh.library.repository.PasswordResetTokenRepository;
import com.trnqngmnh.library.service.TokenService;

class TokenServiceTest {

	@Mock
	private PasswordResetTokenRepository tokenRepository;

	@InjectMocks
	private TokenService tokenService;

	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		user = new User();
		user.setId(1L); // Assuming the user has an ID
	}

	@Test
	void testCreateToken() {
		// Arrange: mock the repository to return null when findByToken is called
		when(tokenRepository.findByToken(anyString())).thenReturn(null);

		// Act: create the token
		PasswordResetToken token = tokenService.createToken(user);

		// Assert: check that the token is not null and properties are set correctly
		assertNotNull(token);
		assertNotNull(token.getToken()); // Token should not be null
		assertEquals(user, token.getUser()); // User should be set
		assertTrue(token.getExpiryDate().after(new Date())); // Expiry should be in the future

		// Verify that the save method was called once
		verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
	}

	@Test
	void testCreateTokenWhenTokenAlreadyExists() {
		// Arrange: mock the repository to return an existing token when findByToken is
		// called
		String existingToken = UUID.randomUUID().toString();
		when(tokenRepository.findByToken(existingToken)).thenReturn(new PasswordResetToken());

		// Arrange again to mock that findByToken will return null after retrying
		when(tokenRepository.findByToken(anyString())).thenReturn(null);

		// Act: create the token
		PasswordResetToken token = tokenService.createToken(user);

		// Assert: check that a new token was generated and returned
		assertNotNull(token);
		assertNotNull(token.getToken()); // New token should be generated
		verify(tokenRepository, times(1)).save(any(PasswordResetToken.class)); // Verify save is called once
	}
}
