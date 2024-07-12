package com.trnqngmnh.library;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderWebRepository extends JpaRepository<OrderWeb, Long>, JpaSpecificationExecutor<OrderWeb> {

	List<OrderWeb> findByUserId(Long userId);

	List<OrderWeb> findByPaymentMethod(String paymentMethod);

}
