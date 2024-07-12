package com.trnqngmnh.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderWebDetailService {
	@Autowired
	private OrderWebDetailRepository orderWebDetailRepository;

//    public List<OrderWebDetail> findByOrderWebId(Long orderId) {
//        return orderWebDetailRepository.findByOrderWebId(orderId);
//    }
}
