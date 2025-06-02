package com.trnqngmnh.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trnqngmnh.library.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	PasswordResetToken findByToken(String token);
}
