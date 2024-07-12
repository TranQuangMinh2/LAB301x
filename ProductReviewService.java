package com.trnqngmnh.library;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductReviewService {
	@Autowired
	private final ProductReviewReposiory productReviewRepository;

	public ProductReviewService(ProductReviewReposiory productReviewRepository) {
		super();
		this.productReviewRepository = productReviewRepository;
	}

	public List<ProductReview> getAllReviews() {
		// TODO Auto-generated method stub
		return productReviewRepository.findAll();
	}

	public void saveReview(ProductReview review) {
		productReviewRepository.save(review);
	}

}
