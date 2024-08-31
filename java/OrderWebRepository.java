package com.trnqngmnh.library;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import jakarta.transaction.Transactional;

public interface OrderWebRepository extends JpaRepository<OrderWeb, Long>, JpaSpecificationExecutor<OrderWeb> {

	List<OrderWeb> findByUserId(Long userId);

	List<OrderWeb> findByPaymentMethod(String paymentMethod);

	@Transactional
	void deleteByCreatedAtBefore(Date date);

	int countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startOfMonth, LocalDateTime endOfMonth);

}
