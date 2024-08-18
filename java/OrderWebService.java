package com.trnqngmnh.library;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderWebService {
	@Autowired
	private final OrderWebRepository orderWebRepository;
	@Autowired
	private OrderWebDetailRepository orderWebDetailRepository;

	public OrderWebService(OrderWebRepository orderWebRepository) {
		this.orderWebRepository = orderWebRepository;
	}

	public List<OrderWeb> getOrdersByUserId(Long userId) {
		return orderWebRepository.findByUserId(userId);
	}

	public List<OrderWeb> getAllOrders() {
		return orderWebRepository.findAll();
	}

	public List<OrderWeb> getOrdersByPaymentMethod(String paymentMethod) {
		return orderWebRepository.findByPaymentMethod(paymentMethod);
	}

	public List<OrderWeb> getOrders(String generalSearch, String paymentMethod, String paymentStatus,
			String deliveryStatus, Integer totalAmount) {
		// Tạo một đối tượng Specification để xây dựng câu truy vấn
		Specification<OrderWeb> spec = Specification.where(null);

		if (generalSearch != null && !generalSearch.isEmpty()) {
			// Thêm điều kiện tìm kiếm chung vào câu truy vấn
//	        spec = spec.and((root, query, cb) -> cb.like(root.get("consignee"), "%" + generalSearch + "%"));
			spec = spec.and((root, query, cb) -> cb.or(cb.like(root.get("consignee"), "%" + generalSearch + "%"),
					cb.like(root.get("deliveryAddress"), "%" + generalSearch + "%")));

		}

		if (paymentMethod != null && !paymentMethod.isEmpty()) {
			// Thêm điều kiện thanh toán vào câu truy vấn
			spec = spec.and((root, query, cb) -> cb.equal(root.get("paymentMethod"), paymentMethod));
		}

		if (paymentStatus != null && !paymentStatus.isEmpty()) {
			// Thêm điều kiện trạng thái thanh toán vào câu truy vấn
			spec = spec.and((root, query, cb) -> cb.equal(root.get("paymentStatus"), paymentStatus));
		}

		if (deliveryStatus != null && !deliveryStatus.isEmpty()) {
			// Thêm điều kiện trạng thái giao hàng vào câu truy vấn
			spec = spec.and((root, query, cb) -> cb.equal(root.get("deliveryStatus"), deliveryStatus));
		}
		if (totalAmount != null) {
			// Thêm điều kiện tổng số tiền vào câu truy vấn
			spec = spec.and((root, query, cb) -> cb.equal(root.get("totalAmount"), totalAmount));
		}
		// Thực hiện truy vấn và trả về kết quả
		return orderWebRepository.findAll(spec);
	}

	public OrderWeb findById(Long orderId) {
		return orderWebRepository.findById(orderId).orElse(null);
	}

	public List<OrderWebDetail> findByOrderWebId(Long orderId) {
		return orderWebDetailRepository.findByOrderWebId(orderId);
	}

	public OrderWeb save(OrderWeb orderWeb) {
		return orderWebRepository.save(orderWeb);

	}

	@Transactional
	public void deleteOldOrders() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		Date oneMonthAgo = calendar.getTime();
		orderWebRepository.deleteByCreatedAtBefore(oneMonthAgo);

	}

}
