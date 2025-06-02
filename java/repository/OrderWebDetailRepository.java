package com.trnqngmnh.library.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trnqngmnh.library.entity.OrderWeb;
import com.trnqngmnh.library.entity.OrderWebDetail;

public interface OrderWebDetailRepository extends JpaRepository<OrderWebDetail, Long> {

	List<OrderWebDetail> findByOrderWebId(Long orderId);

	List<OrderWebDetail> findByOrderWeb(OrderWeb existingOrder);

	@Query("SELECT COALESCE(SUM(owd.quantity), 0) FROM OrderWebDetail owd JOIN owd.orderWeb ow WHERE ow.userId = :userId AND ow.createdAt BETWEEN :startOfMonth AND :endOfMonth")
	int countBooksByUserIdAndCreatedAtBetween(@Param("userId") Long userId,
			@Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

}
