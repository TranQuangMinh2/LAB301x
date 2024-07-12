package com.trnqngmnh.library;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderWebDetailRepository extends JpaRepository<OrderWebDetail, Long> {

	List<OrderWebDetail> findByOrderWebId(Long orderId);

}
